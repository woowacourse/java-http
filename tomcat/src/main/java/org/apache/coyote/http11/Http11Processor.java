package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
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
             final var outputStream = connection.getOutputStream();
             final var requestReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String startLine = requestReader.readLine();
            final var responseBody = parseStartLine(startLine);

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

    private String parseStartLine(String startLine) throws IOException {
        String resourse = startLine.split(" ")[1];
        if (resourse.equals("/")) {
            return "Hello world!";
        }
        File file = new File(getClass().getClassLoader().getResource("static" + resourse).getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
