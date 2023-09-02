package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.StartLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpRequestParser parser = new HttpRequestParser();
    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

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
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            HttpRequest request = parser.parse(bufferedReader);
            StartLine startLine = request.startLine();
            if (startLine.isEmpty()) {
                return;
            }
            String responseBody = "";
            String uri = startLine.uri();
            if (uri.equals("/")) {
                responseBody = "Hello world!";
            } else {
                final URL resource = classLoader.getResource("static" + uri);
                final File file = new File(resource.getFile());
                responseBody = new String(Files.readAllBytes(file.toPath()));
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType(uri) + "charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            bufferedWriter.write(response);
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String contentType(String uri) {
        if (uri.endsWith(".css")) {
            return "text/css;";
        }
        return "text/html;";
    }
}
