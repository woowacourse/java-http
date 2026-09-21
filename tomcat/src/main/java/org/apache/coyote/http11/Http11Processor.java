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
import java.util.Arrays;
import java.util.Map;
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
            "css", "text/css"
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
            String requestFirstLine = bufferedReader.readLine();
            String[] firstLineParts = requestFirstLine.trim().split("\\s+");

            String method = firstLineParts[0];
            String requestTarget = firstLineParts[1];

            URI uri = URI.create(requestTarget);
            String uriPath = uri.getPath();

            // 요청 경로 없을 경우 문자열 반환
            if ("/".equals(uriPath)) {
                final var responseBody = "Hello world!";
                final var response = createResponse("HTTP/1.1 200 OK ", responseBody, "text/html");

                writeResponse(outputStream, response);
            } else if ("/login".equals(uriPath)) {
                String rawQuery = uri.getRawQuery();

                // 이 경우 로그인 여부 확인해서 302 index or 401 반환
                if (rawQuery != null) {
                    Map<String, String> queryParameters = parseQueryParameters(rawQuery);

                    String account = queryParameters.get("account");
                    String password = queryParameters.get("password");

                    InMemoryUserRepository.findByAccount(account)
                            .filter(user -> user.checkPassword(password))
                            .ifPresentOrElse(
                                    user -> {
                                        try {
                                            sendRedirectResponse("/index.html", outputStream);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    },
                                    () -> {
                                        try {
                                            sendErrorResponse("/401.html", outputStream);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            );
                    return;
                }

                // 이경우 그냥 login.html 보여주기
                String loginPath = uriPath + ".html";
                InputStream resourceAsStream = getResourceInputStream(loginPath, outputStream);
                sendResponse(resourceAsStream, "text/html", outputStream);
            } else if ("/register".equals(uriPath)) {
                if ("GET".equals(method)) {
                    String registerFileName = uriPath + ".html";
                    InputStream resourceAsStream = getResourceInputStream(registerFileName, outputStream);
                    sendResponse(resourceAsStream, "text/html", outputStream);
                    return;
                }
                int contentLength = getContentLength(bufferedReader);
                String requestBody = getRequestBody(contentLength, bufferedReader);
                Map<String, String> bodyParameters = parseQueryParameters(requestBody);
                saveUser(bodyParameters);

                try {
                    sendRedirectResponse("/index.html", outputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // 클래스 로더에서 정적 파일 가져오기
                InputStream resourceAsStream = getResourceInputStream(uriPath, outputStream);

                // 확장자에 맞는 content-type 추출
                int pointIndex = uriPath.lastIndexOf('.');
                String fileExtension = uriPath.substring(pointIndex + 1);
                String contentType = MIME_TYPES.get(fileExtension);

                // 정적 파일 반환
                sendResponse(resourceAsStream, contentType, outputStream);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void saveUser(Map<String, String> bodyParameters) {
        String account = bodyParameters.get("account");
        String email = bodyParameters.get("email");
        String password = bodyParameters.get("password");
        InMemoryUserRepository.save(new User(account, email, password));
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
        String requestBody = new String(requestBodyChars, 0, totalRead);
        return requestBody;
    }

    private static int getContentLength(BufferedReader bufferedReader) throws IOException {
        String line;
        int contentLength = 0;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length: ")) {
                contentLength = Integer.parseInt(line.substring("Content-Length: ".length()).trim());
            }
        }
        return contentLength;
    }

    private void sendRedirectResponse(String fileName, OutputStream outputStream) throws IOException {
        String response = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + fileName,
                "Content-Length: 0",
                "", ""
        );
        writeResponse(outputStream, response);
    }

    private void sendErrorResponse(String fileName, OutputStream outputStream) throws IOException {
        String response = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + fileName,
                "Content-Length: 0",
                "", ""
        );
        writeResponse(outputStream, response);
    }

    private static void sendResponse(InputStream resourceAsStream, String contentType, OutputStream outputStream) throws IOException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream)) {
            final var responseBody = new String(bufferedInputStream.readAllBytes(), StandardCharsets.UTF_8);
            final var response = createResponse("HTTP/1.1 200 OK ", responseBody, contentType);
            writeResponse(outputStream, response);
        }
    }

    private InputStream getResourceInputStream(String uriPath, OutputStream outputStream) throws IOException {
        InputStream resourceAsStream = getClass()
                .getClassLoader()
                .getResourceAsStream("static" + uriPath);

        if (resourceAsStream == null) {
            String response = createResponse(
                    "HTTP/1.1 404 Not Found",
                    "Not Found",
                    "text/plain");
            writeResponse(outputStream, response);
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

    private static String createResponse(String statusLine, String responseBody, String contentType) {
        return String.join("\r\n",
                statusLine,
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }

    private static void writeResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
