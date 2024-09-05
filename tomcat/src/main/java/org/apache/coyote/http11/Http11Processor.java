package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.StringTokenizer;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final String request = new String(inputStream.readAllBytes());
            final String response = getResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(String request) throws IOException {
        final StringTokenizer tokenizer = new StringTokenizer(request, "\r\n");

        while (tokenizer.hasMoreTokens()) {
            final String line = tokenizer.nextToken();
            if (line.startsWith("GET")) {
                final String path = line.split(" ")[1];

                if (path.equals("/")) {
                    return getRootPage();
                }

                final URL resource = getClass().getClassLoader().getResource("static" + path);

                final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                return String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }
        }
        return getRootPage();
    }

    private String getRootPage() {
        final var responseBody = "Hello world!";
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
