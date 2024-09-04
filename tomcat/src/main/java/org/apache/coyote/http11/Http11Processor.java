package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;

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
        try (var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            String fileName = getFileName(inputStream);
            var responseBody = getStaticFileContent(fileName);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + getFileExtension(fileName) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getFileName(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return bufferedReader.readLine().split(" ")[1];
    }

    private String getStaticFileContent(String fileName) throws IOException {
        if (Objects.equals(fileName, "/")) {
            return "Hello world!";
        }
        String staticFileName = "static/" + fileName;

        File file = new File(getClass().getClassLoader().getResource(staticFileName).getPath());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String getFileExtension(String fileName) {
        if (Objects.equals(fileName, "/")) {
            return "html";
        }
        String[] splitFileName = fileName.split("\\.");
        return splitFileName[splitFileName.length - 1];
    }
}
