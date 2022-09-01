package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.io.ClassPathResource;
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
             final var outputStream = connection.getOutputStream()) {
            BufferedReader httpReader = new BufferedReader(new InputStreamReader(inputStream));

            String URL = getUrl(httpReader.readLine());
            log.debug("request : {}", URL);

            String response = createResponse(URL);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getUrl(final String requestLine) {
        String[] requests = requestLine.split(" ");
        String URL = requests[1];
        return URL;
    }

    private String createResponse(String URL) throws IOException {
        if (URL.equals("/")) {
            return rootResponse();
        }
        return htmlResponse(URL);
    }

    private String rootResponse() {
        String responseBody = "Hello world!";
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String htmlResponse(final String URL) throws IOException {
        String responseBody = createResponseBody(URL);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createResponseBody(final String URL) throws IOException {
        ClassPathResource resource = new ClassPathResource(URL);
        byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());
        return new String(responseBody);
    }
}
