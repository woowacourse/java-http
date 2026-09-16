package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
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
            String requestEndPoint = parseRequestEndPointFromInputStream(inputStream);
            var responseBody = consistProperBodyContents(requestEndPoint);
            final var response = consistResponseWithBodyAndHeader(responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String consistResponseWithBodyAndHeader(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String consistProperBodyContents(String requestEndPoint) throws URISyntaxException, IOException {
        StringBuilder sb = new StringBuilder();

        if (!requestEndPoint.equals("/")) {
            String fileName = requestEndPoint.replaceFirst("/", "");

            URL resource = getClass().getClassLoader().getResource("static/" + fileName);
            Path path = Path.of(resource.toURI());

            return sb.append(Files.readString(path)).toString();
        }

        return "Hello world!";
    }

    private String parseRequestEndPointFromInputStream(InputStream inputStream) {
        Stream<String> requestInfo = new BufferedReader(new InputStreamReader(inputStream)).lines();
        String firstLineOfHttpRequest = requestInfo.filter(x -> x.contains("HTTP")).findAny().get();
        return firstLineOfHttpRequest.split(" ")[1];
    }
}
