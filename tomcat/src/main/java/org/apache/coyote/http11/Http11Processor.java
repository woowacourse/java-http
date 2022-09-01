package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.UserService;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.ContentFormat;
import org.apache.coyote.http11.model.request.Request;
import org.apache.coyote.http11.model.response.Response;
import org.apache.coyote.http11.model.response.ResponseBody;
import org.apache.coyote.http11.model.response.Status;
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

            Request request = new Request(reader.readLine());
            ResponseBody responseBody = findResource(request.getUrl());
            if (request.getUrl().equals("/login") && !request.getQueryParams().isEmpty()) {
                if (UserService.login(request.getQueryParam("account"), request.getQueryParam("password"))) {
                    log.info(UserService.findByAccount(request.getQueryParam("account")).toString());
                }
            }
            Response response = new Response(Status.OK, responseBody);

            outputStream.write(response.getString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResponseBody findResource(String url) throws IOException {
        if (url.endsWith("/")) {
            url += "/hello.txt";
        }
        if (!url.contains(".")) {
            url += ".html";
        }
        Path path = Path.of(Objects.requireNonNull(this.getClass().getResource("/static" + url)).getPath());
        String body = Files.readString(path);

        ContentFormat contentFormat = ContentFormat.findByExtension(url);

        return new ResponseBody(body, contentFormat);
    }
}
