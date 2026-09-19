package org.apache.coyote.http11;

import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.UriInfo;
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
        String requestPath = "unknown";
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            String url = parseRequestUrl(inputStream);
            UriInfo uriInfo = UriInfo.makeUriInfo(url);
            requestPath = uriInfo.path();

            String resourceUrl = uriInfo.path();
            if ("/login".equals(resourceUrl)) {
                resourceUrl = "/login.html";
            }

            String contentType = findContentType(resourceUrl);
            String responseHeader;
            if ("/login".equals(uriInfo.path()) && uriInfo.hasQueryParameters()) {
                responseHeader = buildLoginResponseHeader(uriInfo);
                outputStream.write(responseHeader.getBytes());
                outputStream.flush();
            } else {
                byte[] responseBody = buildResponseBody(resourceUrl);
                responseHeader = buildResponseHeader(responseBody, contentType);
                outputStream.write(responseHeader.getBytes());
                outputStream.write(responseBody);
                outputStream.flush();
            }
        } catch (IOException | URISyntaxException | RuntimeException e) {
            log.error("HTTP 요청 처리 실패. path={}", requestPath, e);
        }
    }

    private String buildLoginResponseHeader(UriInfo uriInfo) {
        try {
            User user = RequestHandler.findUser(uriInfo.queryParameters());
            log.info("로그인 사용자: {}", user.getAccount());
            return buildRedirectHeader("/index.html");
        } catch (IllegalArgumentException e) {
            return buildRedirectHeader("/401.html");
        }
    }

    private String buildRedirectHeader(String redirectPath) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + redirectPath,
                "Content-Length: 0",
                "",
                "");
    }

    private String buildResponseHeader(
            byte[] responseBody,
            String contentType
    ) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.length + " ",
                "",
                "");
    }

    private byte[] buildResponseBody(String url) throws URISyntaxException, IOException {
        if ("/".equals(url)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }
        String resourcePath = "static" + url;
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        Path path = Path.of(resource.toURI());
        return Files.readAllBytes(path);
    }

    private String parseRequestUrl(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String[] request = reader.readLine().trim().split("\\s+");
        return request[1];
    }

    private String findContentType(String url) {
        if (url.endsWith(".html")) {
            return "text/html";
        }
        if (url.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
