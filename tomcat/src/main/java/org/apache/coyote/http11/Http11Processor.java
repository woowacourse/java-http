package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest httpRequest = readHttpRequest(inputStream);
            String response = buildResponse(httpRequest);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder request = new StringBuilder();
        int contentLength = 0;

        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            request.append(line).append("\r\n");

            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(
                        line.substring("Content-Length:".length()).trim()
                );
            }
        }

        request.append("\r\n");

        char[] body = new char[contentLength];
        reader.read(body);
        request.append(body);

        return new HttpRequest(request.toString());
    }

    private String buildResponse(HttpRequest httpRequest) {
        if ("/login".equals(httpRequest.getPath())) {
            return buildLoginResponse(httpRequest.getQueryParams());
        }
        if ("/".equals(httpRequest.getPath())) {
            return buildRootResponse();
        }

        return buildResourceResponse(httpRequest.getPath());
    }

    private String buildLoginResponse(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");

        if (shouldShowLoginPage(account, password)) {
            return buildResourceResponse("/login.html");
        }

        if (isLoginSuccessful(account, password)) {
            return String.join("\r\n",
                    "HTTP/1.1 302 Found",
                    "Location: /index.html",
                    "",
                    "");
        }

        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /401.html",
                "",
                "");
    }

    private boolean shouldShowLoginPage(
            String account,
            String password
    ) {
        return account == null && password == null;
    }

    private boolean isLoginSuccessful(
            String account,
            String password
    ) {
        return InMemoryUserRepository.findByAccountAndPassword(account, password)
                .isPresent();
    }

    private String buildRootResponse() {
        String responseBody = "Hello world!";

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }

    private String buildResourceResponse(String path) {
        final var resource = findResource(path);
        if (resource == null) {
            throw new RuntimeException("자원을 찾을 수 없습니다.");
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + getContentType(path) + " ",
                "Content-Length: " + resource.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                resource);
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "application/octet-stream";
    }

    private String findResource(String path) {
        try (final var inputStream = Http11Processor.class
                .getClassLoader()
                .getResourceAsStream("static" + path)) {
            if (inputStream == null) {
                return null;
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
