package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
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
             final var outputStream = connection.getOutputStream();
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String requestLine = bufferedReader.readLine();
            String responseBody = "Hello world!";
            String contentType = "text/html";
            if (requestLine != null) {
                String [] strings = requestLine.split(" ");
                if (!strings[1].equals("/")) {
                    final String fileName = "static" + strings[1];
                    final URL resource = getClass().getClassLoader().getResource(fileName);
                    if (resource != null) {
                        responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                        contentType = getContentType(strings[1]);
                    }
                }
            }


            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(String string) {
        if (string.endsWith(".css"))
            return "text/css";
        if (string.endsWith(".js"))
            return "application/javascript";
        return "text/html";
    }
}
