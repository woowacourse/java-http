package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.Headers;
import org.apache.coyote.http11.model.Response;
import org.apache.coyote.http11.model.Status;
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
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String[] lines = reader.readLine().split("\r\n");
            String[] get = lines[0].split(" ");
            String fileName = get[1];

            if (fileName.equals("/")) {
                final var responseBody = "Hello world!";
                List<Header> headers = new ArrayList<>();
                headers.add(new Header("Content-Type", "text/html;charset=utf-8"));
                Response response = new Response(Status.OK, new Headers(headers), responseBody);

                outputStream.write(response.getString().getBytes());
            } else if (fileName.equals("/index.html")) {
                final var responseBody = Files.readString(
                        Path.of(Objects.requireNonNull(this.getClass().getResource("/static" + fileName)).getPath()));

                List<Header> headers = new ArrayList<>();
                headers.add(new Header("Content-Type", "text/html;charset=utf-8"));
                Response response = new Response(Status.OK, new Headers(headers), responseBody);

                outputStream.write(response.getString().getBytes());
            } else if (fileName.equals("/css/styles.css")) {
                final var responseBody = Files.readString(
                        Path.of(Objects.requireNonNull(this.getClass().getResource("/static" + fileName)).getPath()));

                List<Header> headers = new ArrayList<>();
                headers.add(new Header("Content-Type", "text/css;charset=utf-8"));
                Response response = new Response(Status.OK, new Headers(headers), responseBody);

                outputStream.write(response.getString().getBytes());
            } else {
                final var responseBody = Files.readString(
                        Path.of(Objects.requireNonNull(this.getClass().getResource("/static" + fileName)).getPath()));

                List<Header> headers = new ArrayList<>();
                headers.add(new Header("Content-Type", "text/javascript;charset=utf-8"));
                Response response = new Response(Status.OK, new Headers(headers), responseBody);

                outputStream.write(response.getString().getBytes());
            }

            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
