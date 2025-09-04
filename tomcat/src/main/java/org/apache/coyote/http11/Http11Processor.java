package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader br = new BufferedReader(inputStreamReader);

            final String line = br.readLine();
            final String[] requestHeader = line.split(" ");
            final String httpMethod = requestHeader[0];
            final String endPoint = requestHeader[1];

            if (httpMethod.equals("GET") && endPoint.equals("/")) {
                final String response = createResponse("Hello world!");
                writeAndFlush(outputStream, response);
            }

            if (httpMethod.equals("GET") && endPoint.equals("/css/styles.css")) {
                final URL resource = getClass().getClassLoader().getResource("static" + endPoint);
                final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                final String response = createResponseCss(responseBody);
                writeAndFlush(outputStream, response);
            }

            final URL resource = getClass().getClassLoader().getResource("static" + endPoint);
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final var response = createResponse(responseBody);
            writeAndFlush(outputStream, response);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponseCss(final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createResponse(final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private void writeAndFlush(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
