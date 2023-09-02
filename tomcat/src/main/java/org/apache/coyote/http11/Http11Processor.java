package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final int URL_INDEX = 1;
    public static final int FIRST_LINE_INDEX = 0;
    public static final String SPACE = " ";
    public static final String STATIC_RESOURCE_DIR = "static";

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
            final var outputStream = connection.getOutputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);)
        {
            List<String> requestHeader = new ArrayList<>();

            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    return;
                }

                if (line.isEmpty()) {
                    break;
                }

                requestHeader.add(line);
            }

            String firstLine = requestHeader.get(FIRST_LINE_INDEX);
            String requestURL = firstLine.split(SPACE)[URL_INDEX];

            String responseBody = makeResponseBody(requestURL);

            final var response = String.join("\r\n",
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

    private String makeResponseBody(String requestURL) throws IOException {
        String responseBody = "Hello world!";

        if (!requestURL.equals("/")) {
            ClassLoader classLoader = getClass().getClassLoader();
            final URL resource = classLoader.getResource(STATIC_RESOURCE_DIR + requestURL);
            Path path = new File(resource.getFile()).toPath();
            responseBody = new String(Files.readAllBytes(path));
        }
        return responseBody;
    }
}
