package org.apache.coyote.http11;

import javassist.NotFoundException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while (!(line = bufferedReader.readLine()).isBlank()) {
                if (isStartsWithRequestMethod(line)) {
                    final String uri = line.split(" ")[1];
                    final String responseBody = executeController(uri);
                    final String response = makeResponse(responseBody);
                    flushResponse(outputStream, response);
                    return;
                }
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (NotFoundException | URISyntaxException e) {

        }
    }

    private boolean isStartsWithRequestMethod(final String line) {
        for (String requestMethod : List.of("GET", "POST", "PUT", "PATCH", "DELETE")) {
            if (line.startsWith(requestMethod)) {
                return true;
            }
        }
        return false;
    }

    private void flushResponse(final OutputStream outputStream, final String responseBody) throws IOException {
        outputStream.write(responseBody.getBytes());
        outputStream.flush();
    }

    private String makeResponse(final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String executeController(final String uri) throws URISyntaxException, NotFoundException {
        if (uri.equals("/")) {
            return "Hello world!";
        }
        if (uri.contains(".")) {
            final URL resource = getClass().getClassLoader().getResource("static" + uri);
            if (resource == null) {
                throw new NotFoundException("자원을 찾지 못했음 : " + uri);
            }
            return readFile(new File(resource.toURI()));
        }
        return "";
    }

    private String readFile(final File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
