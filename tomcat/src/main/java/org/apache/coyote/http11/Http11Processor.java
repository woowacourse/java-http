package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Http11Processor implements Runnable, Processor {

    private static final Set<String> SUPPORTED_METHODS = Set.of("GET");

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final StaticResourceLoader staticResourceLoader;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.staticResourceLoader = new StaticResourceLoader();
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

            try {
                HttpRequest requestTarget = getRequestTarget(inputStream);
                String requestPath = requestTarget.getHttpPath();

                if ("/login".equals(requestPath)) {
                    logLoginUser(requestTarget);
                }

                StaticResource staticResource = staticResourceLoader.load(requestPath);
                writeResponse(outputStream, "200 OK", staticResource);
            } catch (BadRequestException e) {
                log.warn(e.getMessage());
                StaticResource badRequest = new StaticResource(e.getMessage(), "text/plain");
                writeResponse(outputStream, "400 Bad Request", badRequest);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(OutputStream outputStream, String status, StaticResource resource) throws IOException {
        String responseBody = resource.getBody();

        String response = String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + resource.getContentType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private HttpRequest getRequestTarget(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("HTTP 요청 라인이 존재하지 않습니다.");
        }

        return HttpRequest.from(requestLine, SUPPORTED_METHODS);
    }

    private void logLoginUser(HttpRequest httpRequest) {
        String account = httpRequest.getParams("account");
        String password = httpRequest.getParams("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("login user: {}", user));
    }

}
