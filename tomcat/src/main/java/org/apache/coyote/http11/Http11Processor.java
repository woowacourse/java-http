package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Optional;
import java.util.StringTokenizer;

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

            // 파싱된 HTTP 요청에서 경로 추출
            final String path = parseHttpRequest(inputStream);

            // 경로를 기반으로 정적 파일을 읽고 응답 생성
            final String responseBody = readStaticFile(path);

            //css인 경우 content type을 다르게 준다
            final String contentType = getContentType(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + "charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
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

    private String parseHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = reader.readLine(); // HTTP 요청 라인을 읽음 (예: "GET /index.html HTTP/1.1")

        // 요청 라인을 공백으로 분리하여 경로를 추출
        String[] requestParts = requestLine.split(" ");

        if (requestParts.length >= 2) {
            return requestParts[1]; // 두 번째 요소가 경로 (예: "/index.html")
        } else {
            throw new IOException("Invalid HTTP request"); // 유효하지 않은 요청 처리
        }
    }

    private String readStaticFile(String path) throws IOException {
        // 경로를 기반으로 정적 파일을 읽고 그 내용을 반환하는 로직을 작성해야 합니다.
        // 이 예제에서는 간단하게 파일을 읽어오는 방법을 보여줍니다.
        path = parsePath(path);

        // 클래스 패스에서 정적 파일을 읽을 수 있도록 리소스 로더를 사용
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("static" + path);

        StringBuilder content = new StringBuilder();
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
        }

        // content-type header 추가한 http 응답 변환
        return content.toString();
    }

    private String parsePath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }

        if (path.equals("/login")) {
            path = "/login.html";
        }

        if (path.startsWith("/login?")) {
            isValidUser(path);
            System.out.println(isValidUser(path));
            if (isValidUser(path)) {
                path = "/index.html";
            } else {
                path = "/401.html";
            }
        }

        return path;
    }

    private boolean isValidUser(final String path) {
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
            if (foundUser.checkPassword(parsedPassword)) {
                log.info("user : " + foundUser);
                return true;
            }
        }

        return false;
    }
}
