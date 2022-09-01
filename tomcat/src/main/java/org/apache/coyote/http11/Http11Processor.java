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

            final String s = httpRequest.get(0);
            final String[] firstLine = s.split(" ");
            final String httpMethod = firstLine[0];
            final String targetUri = firstLine[1];

            // 화면 렌더링
            if (targetUri.equals("/index.html") && httpMethod.equals("GET")) {
                render(outputStream, "index.html");
            } else {
                render(outputStream, "404.html");
            }

            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void render(final OutputStream outputStream, final String viewName) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + viewName);
        final byte[] indexHtml = Files.readAllBytes(new File(resource.getFile()).toPath());

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + indexHtml.length + " ",
                "",
                new String(indexHtml));
        outputStream.write(response.getBytes());
    }
}
