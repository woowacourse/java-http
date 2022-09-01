package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.enums.ContentType;
import org.apache.coyote.http11.enums.FilePath;
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
        try (final var inputStream = new InputStreamReader(connection.getInputStream());
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(inputStream)) {

            final String fileName = extractFileName(bufferedReader);
            final String contentType = findContentType(fileName).getValue();
            final String responseBody = FilePath.of(fileName)
                    .generateFile();

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractFileName(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine()
                .split(" ")[1];
    }

    private ContentType findContentType(final String extractedPath) {
        final String[] pathInfos = extractedPath.split("\\.");
        final String fileExtension = pathInfos[pathInfos.length - 1];
        return ContentType.of(fileExtension);
    }
}
