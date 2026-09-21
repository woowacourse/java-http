package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 소켓의 InputStream은 클라이언트가 보낸 HTTP 요청을 바이트로 읽고,
 * OutputStream은 상태 줄, 헤더, 빈 줄, 본문으로 구성된 HTTP 응답을 클라이언트에게 전달한다.
 * HTTP 요청 줄은 "메서드 요청대상 HTTP버전" 순서이며, /index.html은 요청 본문이 아니라 요청 대상 경로이다.
 * 요청 줄과 헤더 같은 텍스트는 Reader로 변환해 해석할 수 있고, 본문은 Content-Type에 따라
 * 텍스트는 문자 인코딩을 적용하고 이미지 등의 바이너리는 바이트 그대로 처리해야 한다.
 * 문자열을 바이트로 인코딩하고 다시 문자로 디코딩할 때는 UTF-8처럼 동일한 인코딩을 명시해야 한다.
 * 파일을 응답할 때 Content-Length에는 문자열의 글자 수가 아니라 실제 응답 본문의 바이트 길이를 사용한다.
 * src/main/resources의 파일은 빌드 시 build/resources/main으로 복사되어 클래스패스에 포함되므로,
 * 실행 위치에 따라 달라지는 상대경로 대신 ClassLoader.getResourceAsStream()으로 읽을 수 있다.
 * BufferedInputStream과 BufferedOutputStream은 기존 스트림을 감싸 버퍼링 기능을 추가하며,
 * flush()는 출력 버퍼의 데이터를 전달하고 close()는 감싼 스트림까지 닫는다.
 * 파일이나 소켓 스트림은 자원 누수를 막기 위해 try-with-resources로 닫아야 한다.
 */
public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "application/javascript"
    );

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            // 요청 경로 읽는 부분
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // 요청줄 읽기
            String requestStartLine = bufferedReader.readLine();
            String[] startLineParts = requestStartLine.trim().split("\\s+");

            String method = startLineParts[0];
            String requestTarget = startLineParts[1];

            // 요청 헤더 읽기
            String line;
            int contentLength = 0;
            HttpCookie httpCookie = new HttpCookie();

            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
                }
                if (line.startsWith("Cookie:")) {
                    String cookieHeader = line.substring("Cookie:".length()).trim();
                    httpCookie = new HttpCookie(cookieHeader);
                }
            }

            // 응답 헤더
            Map<String, String> responseHeaders = new LinkedHashMap<>();
            if (!httpCookie.hasJsessionId()) {
                String sessionId = UUID.randomUUID().toString();
                Session session = new Session(sessionId);
                SessionManager.add(session);
                responseHeaders.put("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/");
            }

            URI uri = URI.create(requestTarget);
            String uriPath = uri.getPath();

            // 요청 경로 없을 경우 문자열 반환
            if ("/".equals(uriPath)) {
                String responseBody = "Hello world!";
                responseHeaders.put("Content-Type", "text/html;charset=utf-8");
                responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));

                String response = createResponse(responseHeaders, "HTTP/1.1 200 OK ", responseBody);
                sendResponse(outputStream, response);
            } else if ("/login".equals(uriPath)) {
                if ("POST".equals(method)) {
                    String requestBody = getRequestBody(contentLength, bufferedReader);
                    Map<String, String> bodyParameters = parseQueryParameters(requestBody);
                    String account = bodyParameters.get("account");
                    String password = bodyParameters.get("password");

                    InMemoryUserRepository.findByAccount(account)
                            .filter(user -> user.checkPassword(password))
                            .ifPresentOrElse(
                                    user -> {
                                        try {
                                            String sessionId = UUID.randomUUID().toString();
                                            Session session = new Session(sessionId);
                                            session.setAttribute("loginUser", user);
                                            SessionManager.add(session);
                                            responseHeaders.put("Location", "/index.html");
                                            responseHeaders.put("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/");
                                            responseHeaders.put("Content-Length", "0");
                                            String response = createResponse(responseHeaders, "HTTP/1.1 302 Found", null);
                                            sendResponse(outputStream, response);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    },
                                    () -> {
                                        try {
                                            responseHeaders.put("Location", "/401.html");
                                            responseHeaders.put("Content-Length", "0");
                                            String response = createResponse(responseHeaders, "HTTP/1.1 302 Found", null);
                                            sendResponse(outputStream, response);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            );
                    return;
                }

                if ("GET".equals(method) && httpCookie.hasJsessionId()) {
                    String jsessionId = httpCookie.getJsessionId();
                    Session session = SessionManager.findSession(jsessionId);
                    if (session != null) {
                        User loginUser = (User) session.getAttribute("loginUser");
                        if (loginUser != null) {
                            responseHeaders.put("Location", "/index.html");
                            responseHeaders.put("Content-Length", "0");
                            String response = createResponse(responseHeaders, "HTTP/1.1 302 Found", null);
                            sendResponse(outputStream, response);
                            return;
                        }
                    }
                }

                String loginPath = uriPath + ".html";
                InputStream resourceAsStream = getResourceInputStream(responseHeaders, loginPath, outputStream);
                if (resourceAsStream == null) {
                    return;
                }
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream)) {
                    String responseBody = new String(bufferedInputStream.readAllBytes(), StandardCharsets.UTF_8);
                    responseHeaders.put("Content-Type", "text/html" + ";charset=utf-8");
                    responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
                    String response = createResponse(responseHeaders, "HTTP/1.1 200 OK ", responseBody);
                    sendResponse(outputStream, response);
                }
            } else if ("/register".equals(uriPath)) {
                if ("GET".equals(method)) {
                    String registerFileName = uriPath + ".html";
                    InputStream resourceAsStream = getResourceInputStream(responseHeaders, registerFileName, outputStream);
                    if (resourceAsStream == null) {
                        return;
                    }
                    try (BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream)) {
                        String responseBody = new String(bufferedInputStream.readAllBytes(), StandardCharsets.UTF_8);
                        responseHeaders.put("Content-Type", "text/html" + ";charset=utf-8");
                        responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
                        String response = createResponse(responseHeaders, "HTTP/1.1 200 OK ", responseBody);
                        sendResponse(outputStream, response);
                    }
                    return;
                }

                if ("POST".equals(method)) {
                    String requestBody = getRequestBody(contentLength, bufferedReader);
                    Map<String, String> bodyParameters = parseQueryParameters(requestBody);
                    User savedUser = saveUser(bodyParameters);

                    String sessionId = UUID.randomUUID().toString();
                    Session session = new Session(sessionId);
                    session.setAttribute("loginUser", savedUser);
                    SessionManager.add(session);

                    responseHeaders.put("Location", "/index.html");
                    responseHeaders.put("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/");
                    responseHeaders.put("Content-Length", "0");

                    String response = createResponse(responseHeaders, "HTTP/1.1 302 Found", null);
                    sendResponse(outputStream, response);
                }

            } else {
                // 클래스 로더에서 정적 파일 가져오기
                InputStream resourceAsStream = getResourceInputStream(responseHeaders, uriPath, outputStream);
                if (resourceAsStream == null) {
                    return;
                }

                // 확장자에 맞는 content-type 추출
                int pointIndex = uriPath.lastIndexOf('.');
                String fileExtension = uriPath.substring(pointIndex + 1);
                String contentType = MIME_TYPES.get(fileExtension);

                // 정적 파일 반환
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream)) {
                    String responseBody = new String(bufferedInputStream.readAllBytes(), StandardCharsets.UTF_8);
                    responseHeaders.put("Content-Type", contentType + ";charset=utf-8");
                    responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
                    String response = createResponse(responseHeaders, "HTTP/1.1 200 OK ", responseBody);
                    sendResponse(outputStream, response);
                }
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static User saveUser(Map<String, String> bodyParameters) {
        String account = bodyParameters.get("account");
        String email = bodyParameters.get("email");
        String password = bodyParameters.get("password");
        return InMemoryUserRepository.save(new User(account, email, password));
    }

    private static String getRequestBody(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] requestBodyChars = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int count = bufferedReader.read(requestBodyChars, totalRead, contentLength - totalRead);
            if (count == -1) {
                break;
            }
            totalRead += count;
        }
        return new String(requestBodyChars, 0, totalRead);
    }

    private InputStream getResourceInputStream(Map<String, String> responseHeaders, String uriPath, OutputStream outputStream) throws IOException {
        InputStream resourceAsStream = getClass()
                .getClassLoader()
                .getResourceAsStream("static" + uriPath);

        if (resourceAsStream == null) {
            responseHeaders.put("Content-Type", "text/plain;charset=utf-8");
            String response = createResponse(responseHeaders, "HTTP/1.1 404 Not Found", "Not Found");
            sendResponse(outputStream, response);
            return null;
        }
        return resourceAsStream;
    }

    private static Map<String, String> parseQueryParameters(String parameters) {
        return Arrays.stream(parameters.split("&"))
                .map(parameter -> parameter.split("=", 2))
                .collect(Collectors.toMap(
                        parts -> decode(parts[0]),
                        parts -> parts.length > 1 ? decode(parts[1]) : ""
                ));
    }

    private static String createResponse(Map<String, String> responseHeaders, String statusLine, String responseBody) {
        StringBuilder response = new StringBuilder();
        response.append(statusLine).append("\r\n");
        responseHeaders.forEach((key, value) ->
                response.append(key)
                        .append(": ")
                        .append(value)
                        .append("\r\n"));

        response.append("\r\n");
        if (responseBody != null) {
            response.append(responseBody);
        }
        return response.toString();
    }

    private static void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
