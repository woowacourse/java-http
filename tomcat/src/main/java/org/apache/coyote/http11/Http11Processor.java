package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
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
             final var outputStream = connection.getOutputStream()) {

            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            final var requestLine = br.readLine();
            log.info("request line: {}", requestLine);

            if (requestLine == null) {
                return;
            }

            final var requestLineTokens = requestLine.split(" ");

            var response = buildHttpResponse("text/html", "Hello world!");
            if (requestLineTokens[1].equals("/index.html")) {
                response = buildHtmlResponse(requestLineTokens[1]);
            }
            if (requestLineTokens[1].equals("/css/styles.css")) {
                response = buildStyleSheetResponse(requestLineTokens[1]);
            }
            if (requestLineTokens[1].equals("/js/scripts.js")) {
                response = buildScriptResponse(requestLineTokens[1]);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String buildHttpResponse(final String fileType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + fileType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String buildResponseBodyFromStaticFile(String fileName) throws IOException {
        final var resourceName = "static" + fileName;
        final var path = Path.of(this.getClass().getClassLoader().getResource(resourceName).getPath());

        return String.join("\n", Files.readAllLines(path)) + "\n";
    }

    private String buildHtmlResponse(final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        return buildHttpResponse("text/html", responseBody);
    }

    private String buildStyleSheetResponse(final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        return buildHttpResponse("text/css", responseBody);
    }

    private String buildScriptResponse(final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        return buildHttpResponse("text/javascript", responseBody);
    }
}
