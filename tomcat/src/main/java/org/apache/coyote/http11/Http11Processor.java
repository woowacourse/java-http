package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.request.HttpBody;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Http11Processor implements Runnable, Processor {

    private static final Set<HttpMethod> SUPPORTED_METHODS = Set.of(HttpMethod.GET);

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

        HttpHeaders headers = readHeaders(reader);
        HttpBody body = readBody(reader, headers.getContentLength());

        return HttpRequest.from(requestLine, headers, body, SUPPORTED_METHODS);
    }

    private HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();

        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            headerLines.add(line);
            line = reader.readLine();
        }

        if (line == null) {
            throw new IOException("HTTP 헤더가 빈 줄로 끝나지 않았습니다.");
        }

        return HttpHeaders.from(headerLines);
    }

    private HttpBody readBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return HttpBody.empty();
        }

        char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                throw new IOException("HTTP body가 Content-Length보다 짧습니다.");
            }
            totalRead += read;
        }

        return new HttpBody(new String(buffer));
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
