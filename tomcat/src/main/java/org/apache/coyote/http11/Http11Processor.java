package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            String uri = httpRequest.get("request URI");

            String responseBody = getResponseBody(uri);
            String contentType = getContentType(uri);

            HttpResponse httpResponse = HttpResponse.from("HTTP/1.1", "200 OK",
                contentType, responseBody);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String uri) throws URISyntaxException, IOException {
        String responseBody = "Hello world!";

        if (!uri.equals("/")) {
            Path path = Paths.get(getClass()
                .getClassLoader()
                .getResource("static" + uri)
                .toURI());

            responseBody = new String(Files.readAllBytes(path));
        }

        return responseBody;
    }

    private String getContentType(String uri) {
        String contentType = "text/html;charset=utf-8";

        if(uri.endsWith("css")){
            contentType = "text/css,*/*;q=0.1";
        }
        return contentType;
    }
}
