package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final Http11Request request = Http11RequestParser.parse(reader);

            StaticResource staticResource;
            String responseStatusCode = "";
            Map<String, String> responseHeaders = new HashMap<>();

            // method + path로 실행할 작업의 분기 처리를 나열합니다.
            if (request.getMethod().equals("GET") && request.getPath().startsWith("/index.html")) {
                staticResource = StaticResourceProvider.getStaticResource("/index.html");
                responseStatusCode = "200 OK";
            } else if (request.getMethod().equals("GET") && request.getPath().startsWith("/login")) {
                staticResource = StaticResourceProvider.getStaticResource("/login.html");
                responseStatusCode = "200 OK";
            } else if (request.getMethod().equals("POST") && request.getPath().startsWith("/login")) {
                if (isLoginSuccess(request.getBodyParam("account"), request.getBodyParam("password"))) {
                    staticResource = StaticResourceProvider.getStaticResource("/index.html");
                    responseStatusCode = "302 Found";
                    responseHeaders.put("Location", "/index.html");
                } else {
                    staticResource = StaticResourceProvider.getStaticResource("/401.html");
                    responseStatusCode = "401 Unauthorized";
                }
            } else {
                staticResource = StaticResourceProvider.getStaticResource(request.getPath());
                responseStatusCode = staticResource != null ? "200 OK" : "404 Not Found";
            }

            final String responseHeader = buildResponseHeader(responseStatusCode, responseHeaders, staticResource);
            outputStream.write(responseHeader.getBytes());
            outputStream.write("\r\n".getBytes());
            outputStream.write(staticResource.getContent());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    // 응답 헤더 문자열을 만들어 반환합니다.
    private String buildResponseHeader(
            final String responseStatusCode,
            final Map<String, String> responseHeaders,
            final StaticResource staticResource
    ) {
        final String CRLF = "\r\n";

        final StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(responseStatusCode).append(CRLF);
        responseHeaders.forEach((key, value) -> sb.append(key).append(": ").append(value).append(CRLF));
        sb.append("Content-Type: ").append(staticResource.getMimeType()).append(CRLF);
        sb.append("Content-Length: ").append(staticResource.getContentLength()).append(CRLF);
        return sb.toString();
    }

    private boolean isLoginSuccess(final String account, final String password) {
        log.debug("account : {} password : {}", account, password);
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        return userOptional
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }
}
