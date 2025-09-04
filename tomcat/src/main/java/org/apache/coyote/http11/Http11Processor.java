package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.httpObject.HttpHeader;
import org.apache.coyote.httpObject.HttpMethod;
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

            final var bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpHeader httpHeader = readHttpHeader(bufferReader);

            final HttpMethod httpMethod = httpHeader.getHttpMethod();
            final String path = httpHeader.getPath();

            if (httpMethod == HttpMethod.GET && path.equals("/")) {
                responseHome(outputStream);
            }

            if (httpMethod == HttpMethod.GET && path.equals("/index.html")) {
                responseIndexHtml(outputStream);
            }

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void responseIndexHtml(final OutputStream outputStream) throws URISyntaxException, IOException {
        final URI uri = getClass().getClassLoader()
                .getResource("static/index.html")
                .toURI();
        final Path htmlPath = Path.of(uri);
        final byte[] read = Files.readAllBytes(htmlPath);
        final String body = new String(read, StandardCharsets.UTF_8);

        final var response = String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body
        );
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private HttpHeader readHttpHeader(final BufferedReader bufferReader) throws IOException {
        final String requestLine = bufferReader.readLine();
        final List<String> headers = new ArrayList<>();
        String headerLine;
        while ((headerLine = bufferReader.readLine()) != null && !headerLine.isEmpty()) {
            headers.add(headerLine);
        }

        return new HttpHeader(requestLine, headers);
    }

    private void responseHome(final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
