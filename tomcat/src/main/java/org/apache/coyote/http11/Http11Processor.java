package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.Status.OK;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.response.Response;
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

            final var requestLines = extractRequestLines(inputStream);
            final var requestURI = extractResourceName(requestLines);
//            Request request = Request.parse(inputStream);
            final var resource = getClass().getClassLoader().getResource("static" + requestURI);

            // TODO 요청 URI 구분하여 다르게 동작하게 하기

            assert resource != null;
            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            final var response = Response.of(OK, "text/html", responseBody).toString();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> extractRequestLines(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> requestLines = new ArrayList<>();

        String line = bufferedReader.readLine();
        if (line == null) {
            return requestLines;
        }
        while (!"".equals(line)) {
            requestLines.add(line);
            log.info("request line {}", line);
            line = bufferedReader.readLine();
        }
        return requestLines;
    }

    private String extractResourceName(List<String> requestLines) {
        String requestHead = requestLines.get(0);
        return requestHead.split(" ")[1];
    }
}
