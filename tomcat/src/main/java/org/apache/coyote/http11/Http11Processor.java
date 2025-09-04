package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

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

            var requestHeader = findRequestHeader(inputStream);
            System.out.println("REQUEST HEADER :" + requestHeader);
            var headers = requestHeader.split(CRLF);
            var firstHeader = headers[0].split(" ");
            var method = firstHeader[0];
            var uri = firstHeader[1];
            var responseBody = findResponseBody(uri);
            var contentType = findContentType(uri);

            final var response = String.join(CRLF,
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String findRequestHeader(final InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder requestHeader = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            requestHeader.append(line).append(CRLF);
        }
        return requestHeader.append(CRLF).toString();
    }

    private String findResponseBody(final String uri) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + uri);
        if (resource == null) {
            return "Hello world!";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String findContentType(final String uri) {
        if (uri.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (uri.endsWith(".css")) {
            return "text/css";
        }
        if (uri.endsWith(".js")) {
            return "application/javascript";
        }
        return "";
    }
}
