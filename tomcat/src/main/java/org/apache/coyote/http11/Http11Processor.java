package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.util.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpHeaderParser parser = new HttpHeaderParser();

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
            final String extractedStrings = parser.extractStrings(inputStream);
            Path filePath = parser.parseFilePath(extractedStrings);
            String fileName = filePath.getFileName().toString();

            List<String> contentLines = Files.readAllLines(filePath);
            contentLines.add("");
            final var response = HttpResponseBuilder.build(fileName, contentLines);

            if (parser.containsQueryParameters(extractedStrings)) {
                Map<String, String> query = parser.extractQueryParameters(extractedStrings);

                User user = InMemoryUserRepository.findByAccount(query.get("account"))
                        .orElseThrow();
                if (user.checkPassword(query.get("password"))) {
                    log.info(user.toString());
                }
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
