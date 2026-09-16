package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final String ROOT_DIRECTORY = "/";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_BODY = "Hello world!";
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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {
            final String target = bufferedReader.readLine()
                .split(" ")[1];

            final String contentType = getContentType(target);
            final String body = readStaticResource(target);

            final String response = generateResponse(body, contentType);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String target) {
        if (target.equals(ROOT_DIRECTORY)) {
            return DEFAULT_CONTENT_TYPE;
        }
        final String prefix = "text/";
        final int lastDotIndex = target.lastIndexOf(".");
        if (lastDotIndex == 0) {
            throw new IllegalArgumentException("유효한 타겟 uri가 아닙니다.");
        }
        return prefix + target.substring(lastDotIndex + 1);
    }

    private String readStaticResource(String target) throws IOException {
        if (target.equals(ROOT_DIRECTORY)) {
            return DEFAULT_BODY;
        }
        final StringBuffer readResource = new StringBuffer();

        final Path path = Path.of(getClass()
            .getResource("/static" + target)
            .getPath());

        String string;
        try (final BufferedReader bufferedReader =
            new BufferedReader(new FileReader(path.toFile()))) {
            while ((string = bufferedReader.readLine()) != null) {
                readResource.append(string);
                readResource.append("\n");
            }
        }

        return readResource.toString();
    }

    private String generateResponse(final String body, final String contentType)  {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: " + contentType + ";charset=utf-8 ",
            "Content-Length: " + body.getBytes().length + " ",
            "",
            body);
    }
}
