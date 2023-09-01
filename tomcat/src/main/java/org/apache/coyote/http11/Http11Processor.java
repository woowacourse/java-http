package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
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
            RequestUri requestUri = getRequestUri(inputStream);
            String response = getResponse(requestUri);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestUri getRequestUri(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String requestUri = bufferedReader.readLine();
        log.info("requestUri: {}", requestUri);
        return RequestUri.from(requestUri);
    }

    private String getResponse(RequestUri requestUri) throws IOException {
        String responseBody = getResponseBody(requestUri);
        return String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    private String getResponseBody(RequestUri requestUri) throws IOException {
        String path = requestUri.getPath();
        HttpMethod httpMethod = requestUri.getHttpMethod();
        if ("/index.html".equals(path) && httpMethod == HttpMethod.GET) {
            return resolveContents(path);
        }
        return "Hello world!";
    }

    private String resolveContents(String path) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(STATIC_PATH + path).getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
