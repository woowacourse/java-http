package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.HttpRequest;
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

            HttpRequest httpRequest = readRequest(inputStream);

            URL resource = getClass().getClassLoader().getResource("static" + httpRequest.getPath());
            Path path = Optional.ofNullable(resource)
                    .map(URL::getPath)
                    .map(Path::of)
                    .orElseThrow(() -> new RuntimeException(httpRequest.getPath() + " not found"));
            List<String> strings = Files.readAllLines(path);

            String responseBody = httpRequest.getPath().endsWith(".html") ? String.join("\r\n", strings) : "Hello world!";
            HttpResponse response = new HttpResponse(1.1, 200, "OK")
                    .addHeader("Content-Type", "text/html;charset=utf-8")
                    .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length))
                    .setBody(responseBody);

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();

        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            sb.append(line).append("\r\n");
            line = reader.readLine();
        }

        return new HttpRequest(sb.toString());
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.getAsBytes());
        outputStream.flush();
    }
}
