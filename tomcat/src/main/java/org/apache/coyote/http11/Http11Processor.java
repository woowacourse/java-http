package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            final var response = responseBuilder(bufferedReader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String responseBuilder(final BufferedReader bufferedReader) throws IOException {
        final String BASIC_RESPONSE_BODY = "Hello world!";
        final String BASIC_RESPONSE = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + BASIC_RESPONSE_BODY.getBytes().length + " ",
                "",
                BASIC_RESPONSE_BODY);

        List<String> request = new ArrayList<>();

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            request.add(line.concat("\r\n"));
        }

        List<String> requestUri = List.of(request.getFirst().split(" "));

        if (requestUri.get(1).equals("/index.html")) {
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "", responseBody
                    );
        }

        return BASIC_RESPONSE;
    }
}
