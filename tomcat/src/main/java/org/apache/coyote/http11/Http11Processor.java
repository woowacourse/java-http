package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var requestHeader = getRequestHeader(bufferedReader);
            log.info("requestHeader ::: {}", requestHeader);
            final var url = requestHeader.split(" ")[1];
            final var response = getResponse(url);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRequestHeader(final BufferedReader bufferedReader) throws IOException {
        StringBuilder header = new StringBuilder();

        while (bufferedReader.ready()) {
            header.append(bufferedReader.readLine())
                    .append("\r\n");
        }

        return header.toString();
    }

    private String getResponse(String url) throws IOException {
        if ("/".equals(url)) {
            final var responseBody = "Hello world!";

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if ("/index.html".equals(url)) {
            final URL resource = getClass().getClassLoader().getResource("static/index.html");

            return "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 5564 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        }

        if (url.contains("/css")) {
            final URL resource = getClass().getClassLoader().getResource("static" + url);
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            return "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/css;charset=utf-8 \r\n" +
                    "Content-Length:" + responseBody.getBytes().length + " \r\n" +
                    "\r\n" +
                    responseBody;
        }

        if (url.contains(".js")) {
            final URL resource = getClass().getClassLoader().getResource("static" + url);
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            return "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: application/javascript;charset=utf-8 \r\n" +
                    "Content-Length:" + responseBody.getBytes().length + " \r\n" +
                    "\r\n" +
                    responseBody;
        }

        throw new IllegalArgumentException("올바르지 않은 URL 요청입니다.");
    }
}
