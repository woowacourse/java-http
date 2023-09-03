package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = br.readLine(); // HTTP 요청 라인을 읽음 (예: "GET /index.html HTTP/1.1")
            final String httpMethod = parseHttpMethod(requestLine);
            final Map<String, String> headers = parseRequestHeaders(br);

            //post method일 때만 requestBody 읽음
            if ("POST".equals(httpMethod)) {
                final String requestBody = readRequestBody(br, headers);
                // 이제 requestBody 변수에 POST 요청의 request body가 저장되어 있음
                registerUser(requestBody);
            }

            String jsessionId="";
            if (isContainJsessionId(headers)) {
               jsessionId = parseJsessionId(headers);
            }

            // 파싱된 HTTP 요청에서 경로 추출
            final String path = parseHttpRequest(requestLine);

            // 경로를 기반으로 정적 파일을 읽고 응답 생성
            final String parsedPath = parsePath(path, httpMethod, jsessionId);
            final String responseBody = readStaticFile(path, httpMethod, parsedPath);

            //css인 경우 content type을 다르게 준다
            final String contentType = getContentType(path);

            // JSESSIONID가 있는 경우, 없는 경우 다르게 response를 준다
            final var response = getResponse(path, contentType, responseBody, headers, parsedPath);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(String path, final String contentType, final String responseBody, final Map<String, String> headers, final String parsedPath) {
        if (path.contains("/login?") && parsedPath.equals("/index.html") && !isContainJsessionId(headers)) {
            final String jsessionId = UUID.randomUUID().toString();
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Set-Cookie: " + "JSESSIONID=" + jsessionId + " ",
                    "Content-Type: " + contentType + "charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + "charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

    }

    private boolean isContainJsessionId(final Map<String, String> headers) {
        if (headers.containsKey("Cookie")) {
            final String cookieValue = headers.get("Cookie");
            String[] cookiePairs = cookieValue.split("; ");
            for (String cookiePair : cookiePairs) {
                String[] parts = cookiePair.split("=");
                if (parts[0].equals("JSESSIONID")) {
                    return true;
                }
            }
        }

        return false;
    }

    private String parseJsessionId(final Map<String, String> headers) {
        if (headers.containsKey("Cookie")) {
            final String cookieValue = headers.get("Cookie");
            String[] cookiePairs = cookieValue.split("; ");
            for (String cookiePair : cookiePairs) {
                String[] parts = cookiePair.split("=");
                if (parts[0].equals("JSESSIONID")) {
                    return parts[1];
                }
            }
        }

       return "";
    }

    private String readRequestBody(BufferedReader br, Map<String, String> headers) throws IOException {
        final int contentLength = Integer.parseInt(headers.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private Map<String, String> parseRequestHeaders(final BufferedReader br) throws IOException {
        //request Header들 읽어오기
        Map<String, String> headers = new HashMap<>();

        final List<String> lines = new ArrayList<>();
        String line;
        while (!(line = br.readLine()).equals("")) {
            lines.add(line);
        }

        int emptyLineIndex = lines.indexOf("");
        if (emptyLineIndex == -1) {
            emptyLineIndex = lines.size();
        }

        String line2;
        for (int i = 0; i < emptyLineIndex; i++) {
            line2 = lines.get(i);
            int colonIndex = line2.indexOf(':');
            if (colonIndex != -1) {
                String key = line2.substring(0, colonIndex).trim();
                String value = line2.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    private String getContentType(final String path) {
        final String contentType;
        if (path.endsWith(".css")) {
            contentType = "text/css;charset=utf-8";
        } else {
            contentType = "text/html;charset=utf-8";
        }
        return contentType;
    }

    private String parseHttpRequest(String requestLine) throws IOException {
        // 요청 라인을 공백으로 분리하여 경로를 추출
        String[] requestParts = requestLine.split(" ");

        if (requestParts.length >= 2) {
            return requestParts[1]; // 두 번째 요소가 경로 (예: "/index.html")
        } else {
            throw new IOException("Invalid HTTP request"); // 유효하지 않은 요청 처리
        }
    }

    private String parseHttpMethod(String requestLine) throws IOException {
        // 요청 라인을 공백으로 분리하여 경로를 추출
        String[] requestParts = requestLine.split(" ");
        return requestParts[0];
    }

    private String readStaticFile(String path, String httpMethod, String parsedPath) throws IOException {
        // 경로를 기반으로 정적 파일을 읽고 그 내용을 반환하는 로직을 작성해야 합니다.
        // 이 예제에서는 간단하게 파일을 읽어오는 방법을 보여줍니다.

        // 클래스 패스에서 정적 파일을 읽을 수 있도록 리소스 로더를 사용
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resourceInputStream = classLoader.getResourceAsStream("static" + parsedPath);

        StringBuilder content = new StringBuilder();
        if (resourceInputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceInputStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
        }

        // content-type header 추가한 http 응답 변환
        return content.toString();
    }

    private String parsePath(String path, String httpMethod, String jsessionId) {
        if (path.equals("/")) {
            return "/index.html";
        }

        if (path.equals("/login")) {
            if(SessionManager.validJsession(jsessionId) && !jsessionId.equals("")) {
                path = "/index.html";
            } else {
                path = "/login.html";
            }
        }

        if (path.startsWith("/login?")) {
            if (isValidUser(path, jsessionId)) {
                path = "/index.html";
            } else {
                path = "/401.html";
            }
        }

        if (path.equals("/register")) {
            path = "/register.html";
        }

        if (path.startsWith("/register") && httpMethod.equals("POST")) {
            path = "/index.html";
        }

        return path;
    }

    private boolean isValidUser(final String path, String jsessionId) {
        final StringTokenizer st = new StringTokenizer(path, "&");
        String parsedAccount = "";
        String parsedPassword = "";

        while (st.hasMoreTokens()) {
            final String token = st.nextToken();

            if (token.startsWith("/login?account=")) {
                parsedAccount = token.substring("/login?account=".length());
            } else if (token.startsWith("password=")) {
                parsedPassword = token.substring("password=".length());
            }
        }

        final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(parsedAccount);
        if (maybeUser.isPresent()) {
            final User foundUser = maybeUser.get();
            if (foundUser.checkPassword(parsedPassword) && !SessionManager.validUser(foundUser)) {
                log.info("로그인 성공! 아이디 : " + parsedAccount);
                SessionManager.add(foundUser, jsessionId);
                return true;
            }
        }

        return false;
    }

    private void registerUser(final String requestBody) throws IOException {
        final StringTokenizer st = new StringTokenizer(requestBody, "&");
        String parsedAccount = "";
        String parsedEmail = "";
        String parsedPassword = "";

        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (token.startsWith("account=")) {
                parsedAccount = token.substring("account=".length());
            } else if (token.startsWith("email=")) {
                parsedEmail = token.substring("email=".length());
            } else if (token.startsWith("password=")) {
                parsedPassword = token.substring("password=".length());
            }
        }

        final User user = new User(parsedAccount, parsedPassword, parsedEmail);
        InMemoryUserRepository.save(user);

        log.info("유저 저장 성공!");
    }
}
