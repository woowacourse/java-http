package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.parser.ContentParseResult;
import org.apache.coyote.http11.parser.Http11GetProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Http11GetProcessor http11GetProcessor;

    public Http11Processor(final Socket connection) {
        this.http11GetProcessor = new Http11GetProcessor();
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String httpRequests = readGetFromReader(bufferedReader);

            ContentParseResult parseResult = http11GetProcessor.parse(httpRequests);
            byte[] parsedContent = parseResult.getParseContent();

            byte[] response = String.join(
                            "\r\n",
                            "HTTP/1.1 200 OK ",
                            parseResult.getAdditionalResponse(),
                            "Content-Length: " + parsedContent.length + " ",
                            "",
                            new String(parsedContent)
                    )
                    .getBytes();

            outputStream.write(response);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readGetFromReader(final BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();

        String buffer = "";
        while (!(buffer = bufferedReader.readLine()).isEmpty()) {
            String[] lineSplit = buffer.split(" ");

            if (buffer.startsWith("GET") && lineSplit.length >= 2) {
                return parseContentPath(buffer);
            }
        }

        throw new IllegalArgumentException("유효하지 않은 요청입니다");
    }

    private String parseContentPath(String buffer) {
        return buffer.split(" ")[1];
    }
}
