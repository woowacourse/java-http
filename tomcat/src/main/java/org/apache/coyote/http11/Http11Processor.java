package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
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

            final var response = getResponse(inputStream);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String getResponse(InputStream inputStream) throws IOException, URISyntaxException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final var stringBuilder = new StringBuilder();

        while (bufferedReader.ready()) {
            final var readLine = bufferedReader.readLine();
            stringBuilder.append(readLine).append("\r\n");
        }

        final var request = stringBuilder.toString();
        log.info("request : {}", request);
        final var requestSplit = request.split(" ");
        final var resourcePath = requestSplit[1];
        final var resource = getClass().getClassLoader().getResource("static" + resourcePath);
        final var path = Paths.get(Objects.requireNonNull(resource).toURI());

        final var responseBody = Files.readString(path);
        log.info("responseBody : {}", responseBody);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
