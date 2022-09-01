package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.utils.FileUtils;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        final int REQUEST_LINE_INDEX = 0;
        try (final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream()) {
            final List<String> request = extractRequest(inputStream);
            HttpRequest httpRequest = HttpRequest.from(request.get(REQUEST_LINE_INDEX));
            String responseBody = handler(httpRequest);
            outputStream.write(writeResponseOk(ContentType.from(httpRequest.getFileExtension()).getMediaType(), responseBody));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> extractRequest(InputStream inputStream) throws IOException {
        List<String> request = new ArrayList<>();
        final BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            request.add(line);
        }
        return request;
    }
    private String handler(HttpRequest httpRequest) throws IOException {
        String path = httpRequest.getPath();
        if ("/".equals(path)) {
            return "Hello world!";
        }
        if ("/login".equals(path)) {
            LoginHandler.login(httpRequest.getQueryParams());
            return FileUtils.readFile(getResource("/login.html"));
        }
        return FileUtils.readFile(getResource(path));
    }

    private URL getResource(String uri) {
        URL resource = getClass().getClassLoader()
            .getResource("static" + uri);
        if (resource == null) {
            log.error("올바르지 않은 경로: " + uri);
            return getResource("/404.html");
        }
        return resource;
    }

    private byte[] writeResponseOk(String contentType, String responseBody) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: " + contentType + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody).getBytes();
    }
}
