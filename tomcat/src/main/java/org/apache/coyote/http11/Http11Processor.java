package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int MAX_REQUEST_SIZE = 104_857_600; // 10MB

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
            // request
            String request = parseRequest(inputStream);
            String header = request.split("\r\n")[0];
            validateHeader(header);
            String[] words = header.split(" ");
            String requestPath = words[1].split("\\?")[0];
            HttpMethod httpMethod = HttpMethod.from(words[0]);
            QueryParameters params = parseParameters(words[1]);

            // response
            ContentType contentType = ContentType.NONE;
            HttpStatus httpStatus = HttpStatus.OK;
            Headers headers = new Headers();
            String responseBody = "";

            try {
                if (requestPath.equals("/login")) {
                    if (params.isEmpty()) {
                        contentType = ContentType.HTML;
                        requestPath = "/login.html";
                    } else {
                        login(params);
                        httpStatus = HttpStatus.FOUND;
                        contentType = ContentType.HTML;
                        headers.put("Location", "/index.html");
                    }
                }

                if (requestPath.endsWith(".html")) {
                    contentType = ContentType.HTML;
                }
                if (requestPath.endsWith(".css")) {
                    contentType = ContentType.CSS;
                }
                if (requestPath.endsWith(".js")) {
                    contentType = ContentType.JAVASCRIPT;
                }
            } catch (UnauthorizedException e) {
                contentType = ContentType.HTML;
                httpStatus = HttpStatus.UNAUTHORIZED;
                headers.clear();
                requestPath = "/401.html";
            } catch (IllegalArgumentException e) {
                contentType = ContentType.HTML;
                httpStatus = HttpStatus.NOT_FOUND;
                headers.clear();
                requestPath = "/404.html";
            }

            if (contentType.isText() && !httpStatus.is3xx()) {
                responseBody = getStaticPage(requestPath);
            }
            final var response = buildResponse(httpStatus, contentType, headers, responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void validateHeader(String header) {
        if (header.split(" ").length < 3) {
            throw new IllegalArgumentException("유효하지 않은 요청 포맷입니다.");
        }
    }

    private String buildResponse(HttpStatus status, ContentType contentType, Headers headers, String responseBody) {
        int bodyLength = getBodyLength(responseBody);
        return String.join("\r\n",
            "HTTP/1.1 " + status.getCode() + " " + status.getName(),
            "Content-Type: " + contentType.getType() + ";charset=utf-8",
            "Content-Length: " + bodyLength,
            headers.toString(),
            "",
            responseBody);
    }

    private int getBodyLength(String responseBody) {
        if (responseBody == null) {
            return 0;
        }
        return responseBody.getBytes().length;
    }

    private String getStaticPage(String requestPath) throws IOException, URISyntaxException {
        String normalizedPath = Paths.get(requestPath).normalize().toString();
        if (normalizedPath.contains("..")) {
            throw new IllegalArgumentException("존재하지 않는 페이지입니다.");
        }
        if (normalizedPath.equals("/") || normalizedPath.equals("\\")) {
            return "Hello world!";
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static" + normalizedPath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("존재하지 않는 페이지입니다.");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void login(QueryParameters queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        User user = findUser(account, password);
        log.info(user.toString());
    }

    private User findUser(String account, String password) {
        if (account == null || password == null) {
            throw new UnauthorizedException("필수 정보가 누락되었습니다.");
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            throw new UnauthorizedException("존재하지 않는 사용자입니다.");
        }
        if (!user.get().checkPassword(password)) {
            throw new UnauthorizedException("비밀번호가 틀렸습니다.");
        }
        return user.get();
    }

    private QueryParameters parseParameters(String url) {
        QueryParameters queryParameters = new QueryParameters();
        String[] words = url.split("\\?");
        if (words.length > 1) {
            for (var p : words[1].split("&")) {
                queryParameters.put(p);
            }
        }
        return queryParameters;
    }

    private String parseRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        int byteSum = 0;
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            sb.append(line).append("\r\n");
            byteSum += line.length() + 2;
            if (byteSum > MAX_REQUEST_SIZE) {
                throw new IllegalArgumentException("최대 크기를 초과한 요청입니다.");
            }
        }

        return sb.toString();
    }
}
