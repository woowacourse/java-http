package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final ClassLoader CLASS_LOADER = Http11Processor.class.getClassLoader();

    private static final String CRLF = "\r\n";
    private static final String STATIC_RESOURCES_PATH = "static";
    private static final String DEFAULT_INDEX = "index.html";
    private static final Map<String, String> DEFAULT_RESPONSES = Map.of(
            "/", "Hello world!"
    );

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
            HttpRequest httpRequest = HttpRequest.from(inputStream);

            final String responseBody = readFileFrom(httpRequest.getTarget());
            final var response = String.join(CRLF,
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readFileFrom(final String target) throws IOException {
        if (DEFAULT_RESPONSES.containsKey(target)) {
            return DEFAULT_RESPONSES.get(target);
        }
        Path resourcePath = getResourceFileFrom(target);
        return Files.readString(resourcePath);
    }

    private Path getResourceFileFrom(final String target) {
        URL resource = CLASS_LOADER.getResource(getRelativePathFrom(target));
        validatePresenceOf(resource);

        String absolutePath = resource.getFile();
        return new File(absolutePath).toPath();
    }

    private void validatePresenceOf(final URL resource) {
        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("리소스를 찾지 못했습니다.");
        }
    }

    private String getRelativePathFrom(final String target) {
        if (target.endsWith("/")) {
            return STATIC_RESOURCES_PATH + target + DEFAULT_INDEX;
        }
        return STATIC_RESOURCES_PATH + target;
    }
}
