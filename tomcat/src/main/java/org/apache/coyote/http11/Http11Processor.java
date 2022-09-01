package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            // HTTP 요청 읽기
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final List<String> httpRequest = new ArrayList<>();

            String line = bufferedReader.readLine();
            httpRequest.add(line);
            while (!"".equals(line)) {
                if (line == null) {
                    return;
                }
                line = bufferedReader.readLine();
                httpRequest.add(line);
            }

            final String firstLine = httpRequest.get(0);
            final String[] split = firstLine.split(" ");
            final String httpMethod = split[0];
            final String requestUri = split[1];

            log.info(String.join("\r\n", httpRequest));

            // 화면 렌더링
            render(outputStream, requestUri);

            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void render(final OutputStream outputStream, final String requestUri) throws IOException {
        // static 파일
        final String viewName = "static" + requestUri;
        String contentType = "text/html";
        if (requestUri.endsWith("css")) {
            contentType = "text/css";
        } else if (requestUri.endsWith("js")) {
            contentType = "application/javascript";
        } else if (requestUri.endsWith("svg")){
            contentType = "image/svg+xml";
        }

        URL resource = getClass().getClassLoader().getResource(viewName);
        if (resource == null) {
            resource = getClass().getClassLoader().getResource("static/404.html");
        }

        final byte[] indexHtml = Files.readAllBytes(new File(resource.getFile()).toPath());

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + indexHtml.length + " ",
                "",
                new String(indexHtml));
        outputStream.write(response.getBytes());
    }
}
