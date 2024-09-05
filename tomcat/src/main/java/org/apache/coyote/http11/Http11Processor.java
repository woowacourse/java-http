package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.component.HttpRequest;
import org.apache.coyote.http11.component.HttpResponse;
import org.apache.coyote.http11.component.ResponseHeader;
import org.apache.coyote.http11.component.ResponseLine;
import org.apache.coyote.http11.component.StatusCode;
import org.apache.coyote.http11.component.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

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
             final var inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final var bufferedReader = new BufferedReader(inputStreamReader)) {

            final var plaintext = bufferedReader.readLine();

            if (plaintext == null) {
                return;
            }

            final HttpRequest httpRequest = new HttpRequest(plaintext);

            if (httpRequest.getUri().toString().equals("/")) {
                renderWelcome(outputStream);
            } else {
                extracted(httpRequest.getUri().toString(), outputStream);
            }

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void renderWelcome(final OutputStream outputStream) throws IOException {
        final String resource = "Hello world!";
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + resource.getBytes().length + " ",
                "",
                resource);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void extracted(final String path, final OutputStream outputStream) throws IOException {
        final var resource = getClass().getClassLoader().getResource("static/" + path);
        final var ok = new ResponseLine(new Version(1, 1), new StatusCode("OK", 200));
        final var responseHeader = new ResponseHeader();
        final var response = new HttpResponse(ok, responseHeader,
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
        outputStream.write(response.getResponseText().getBytes());
        outputStream.flush();
    }
}
