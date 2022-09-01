package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final String requestStartLine = bufferedReader.readLine();
            final String[] splitRequestStartLine = requestStartLine.split(" ");
            final String requestUri = splitRequestStartLine[1];

            final Map<String, String> headers = new HashMap<>();
            String header;
            while (!"".equals((header = bufferedReader.readLine()))) {
                final String[] splitHeader = header.split(": ");
                headers.put(splitHeader[0], splitHeader[1]);
            }

            String responseBody;
            if (requestUri.equals("/")) {
                responseBody = "Hello world!";
            } else {
                final String resource = getClass().getClassLoader()
                        .getResource("static" + requestUri)
                        .getPath();
                final File file = new File(resource);
                try (final BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                    responseBody = fileReader.lines()
                            .collect(Collectors.joining("\n"));
                    responseBody += "\n";
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
