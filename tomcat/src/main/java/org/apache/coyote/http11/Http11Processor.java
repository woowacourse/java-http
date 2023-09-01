package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_PATH = "static";

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
        try (InputStream inputStream = connection.getInputStream();
            OutputStream outputStream = connection.getOutputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            RequestUri requestUri = getRequestUri(bufferedReader);
            String response = getResponse(requestUri);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestUri getRequestUri(BufferedReader bufferedReader) throws IOException {
        String requestUri = bufferedReader.readLine();
        log.info("requestUri: {}", requestUri);
        return RequestUri.from(requestUri);
    }

    private String getResponse(RequestUri requestUri) throws IOException {
        String responseBody = getResponseBody(requestUri);
        String contentType = resolveContentType(requestUri);
        return String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: " + contentType + " ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            System.lineSeparator()) + responseBody;
    }

    private String getResponseBody(RequestUri requestUri) throws IOException {
        String path = requestUri.getPath();
        HttpMethod httpMethod = requestUri.getHttpMethod();
        if (path.endsWith(".html") && httpMethod == HttpMethod.GET) {
            return resolveContents(path);
        }
        if (path.endsWith(".css") && httpMethod == HttpMethod.GET) {
            return resolveContents(path);
        }
        if (path.endsWith(".js") && httpMethod == HttpMethod.GET) {
            return resolveContents(path);
        }
        return "Hello world!";
    }

    private String resolveContents(String path) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(STATIC_PATH + path);
        if (resource == null) {
            throw new IllegalArgumentException("해당 경로의 파일을 찾을 수 없습니다.");
        }
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String resolveContentType(RequestUri requestUri) {
        String path = requestUri.getPath();
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "*/*";
    }
}
