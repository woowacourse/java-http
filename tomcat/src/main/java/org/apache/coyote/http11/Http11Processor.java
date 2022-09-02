package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.util.HttpParser;
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
             final var outputStream = connection.getOutputStream()) {
            HttpParser httpParser = new HttpParser(inputStream);
            HttpMethod httpMethod = httpParser.getHttpMethod();
            String httpUrl = httpParser.getHttpUrl();

            String responseBody = "";
            if (httpUrl.equals("/")) {
                responseBody = "Hello world!";
            }

            if (httpMethod.equals(HttpMethod.GET) && httpUrl.endsWith(".html")) {
                File file = getFile(httpUrl);
                responseBody = toResponseBody(file);
            }

            outputStream.write(toResponse(responseBody).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private File getFile(String httpUrl) throws URISyntaxException {
        String fileName = httpUrl.substring(1);
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("static/" + fileName);

        return Paths.get(resource.toURI()).toFile();
    }

    private String toResponseBody(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String nextLine = "";
        while ((nextLine = bufferedReader.readLine()) != null) {
            stringBuilder.append(nextLine);
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private String toResponse(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
