package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
            String fileName = bufferedReader.readLine().split(" ")[1];

            String responseBody = "Hello world!";
            String last = "plain";

            if (!fileName.equals("/")) {
                URL resource = getClass().getClassLoader().getResource("static/" + fileName.substring(1));
                if (!fileName.equals("/index.html")) {
                    resource = getClass().getClassLoader().getResource("static/" + fileName.substring(1));
                }
                System.out.println(fileName.substring(1) + "   " + resource);
                final Path path = new File(resource.getFile()).toPath();
                final List<String> actual = Files.readAllLines(path);
                responseBody = String.join("\n", actual) + "\n";
                last = fileName.split("\\.")[1];
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/"+ last + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
