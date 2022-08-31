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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String responseBody = findResponseBody(reader);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String findResponseBody(BufferedReader reader) {
        String path = parsePath(reader);
        if (isRootRequest(path)) {
            return "Hello world!";
        }
        return ConvertToString(findStaticFile(path));
    }

    private String ConvertToString(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File findStaticFile(String path) {
        final URL resource = getClass().getClassLoader().getResource("static/" + path);
        return new File(resource.getFile());
    }

    private String parsePath(BufferedReader reader) {
        try {
            String startLine = reader.readLine();
            return startLine.split(" ")[1];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isRootRequest(String path) {
        return path.equals("/");
    }
}
