package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String fileName = getFileName(bufferedReader);
            final var responseBody = getResponseBody(fileName);

            final String contentType = getContentType(fileName);
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getFileName(final BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine().split(" ")[1].substring(1);
        } catch (final IOException e) {
            return "";
        }
    }

    private String getResponseBody(final String fileName) throws IOException {
        if (fileName.isEmpty()) {
            return "Hello world!";
        }
        final String path = getClass().getClassLoader().getResource("static/" + fileName).getPath();
        final FileInputStream fileInputStream = new FileInputStream(path);

        final String responseBody = new String(fileInputStream.readAllBytes());
        fileInputStream.close();

        return responseBody;
    }

    private String getContentType(final String fileName) {
        if (fileName.endsWith("css")) {
            return "text/css";
        }
        return "text/html";
    }
}
