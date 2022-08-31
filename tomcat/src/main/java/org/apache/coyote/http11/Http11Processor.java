package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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

            String responseBody = "Hello world!";
            String contentType = "text/html";

            final String httpStartLine = bufferedReader.readLine();
            if (httpStartLine == null) {
                return;
            }
            String requestUrl =  httpStartLine.split(" ")[1];

            if (requestUrl.equals("/")) {
                contentType = makeContentType(contentType, requestUrl);
                responseBody = new String(readDefaultFile(requestUrl), UTF_8);
            }

            if (requestUrl.contains(".html") || requestUrl.contains(".css") || requestUrl.contains(".js")) {
                contentType = makeContentType(contentType, requestUrl);
                responseBody = new String(readAllFile(requestUrl), UTF_8);
            }

            if (!requestUrl.contains(".")) {
                requestUrl = requestUrl + ".html";
                contentType = makeContentType(contentType, requestUrl);
                responseBody = new String(readAllFile(requestUrl), UTF_8);
            }

            final String response = makeResponse(responseBody, contentType);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String makeContentType(String contentType, final String requestUrl) {
        if (requestUrl.contains(".css")) {
            contentType = "text/" + requestUrl.split("\\.")[1];
        }
        if (requestUrl.contains(".js")) {
            contentType = "application/javascript";
        }
        return contentType;
    }

    private static byte[] readAllFile(final String requestUrl) throws IOException {
        final URL resourceUrl = ClassLoader.getSystemResource("static" + requestUrl);
        final Path path = new File(resourceUrl.getPath()).toPath();
        return Files.readAllBytes(path);
    }

    private static byte[] readDefaultFile(final String requestUrl) throws IOException {
        final URL resourceUrl = ClassLoader.getSystemResource("static/index.html");
        final Path path = new File(resourceUrl.getPath()).toPath();
        return Files.readAllBytes(path);
    }

    private static String makeResponse(final String responseBody, final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
