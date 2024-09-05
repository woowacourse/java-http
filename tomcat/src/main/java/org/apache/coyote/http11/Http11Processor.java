package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.util.FileReader;
import org.apache.coyote.util.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestParser parser = new HttpRequestParser();

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
             final var outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = parser.extractRequest(inputStream);
            final String fileName = getFilePath(request.getPath());
            final Path filePath = FileReader.parseFilePath(fileName);

            final List<String> contentLines = FileReader.readAllLines(filePath);

            final HttpResponse response = HttpResponseBuilder.build(
                    getFilePath(request.getPath()),
                    contentLines,
                    HttpStatus.OK
            );
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getFilePath(String path) {
        if (path.isEmpty()) {
            return "index.html";
        }
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }
}
