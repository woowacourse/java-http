package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
            final var clientReader = new BufferedReader(new InputStreamReader(inputStream));
            final var startLine = clientReader.readLine();
            final var response = createHttpResponse(loadStaticFile(startLine));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponseData loadStaticFile(String startLine) throws IOException {
        final var startLines = startLine.split(" ");
        if ("/".equals(startLines[1])) {
            return new HttpResponseData("Hello world!".getBytes(), "text/html;charset=utf-8");
        }
        final var requestResource = startLines[1].split("/");
        final var resourceName = requestResource[requestResource.length - 1];
        final var resourcePath = getClass().getClassLoader().getResource("static/" + resourceName).getPath();
        final var bufferedInputStream = new BufferedInputStream(new FileInputStream(resourcePath));
        final var responseBody = bufferedInputStream.readAllBytes();
        bufferedInputStream.close();

        return new HttpResponseData(responseBody, "text/html;charset=utf-8");
    }

    private String createHttpResponse(HttpResponseData httpResponseData) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + httpResponseData.contentType + " ",
                "Content-Length: " + httpResponseData.responseBody.length + " ",
                "",
                new String(httpResponseData.responseBody, StandardCharsets.UTF_8));
    }

    private static record HttpResponseData(byte[] responseBody, String contentType) {
    }

    ;
}
