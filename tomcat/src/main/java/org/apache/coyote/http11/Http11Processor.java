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
import org.apache.coyote.http11.utils.ResourceMatcher;
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

            Request request = Request.from(reader.readLine());
            login(request);

            Response response = Response.of(Status.OK);
            response.addResponseBody(findResource(request.getUrl()));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void login(final Request request) {
        if (UserService.process(request.getUrl(), request.getQueryParams())) {
            log.info(UserService.findByAccount(request.getQueryParams().get("account")).toString());
        }
    }

    private ResponseBody findResource(final String url) throws IOException {
        String fileName = ResourceMatcher.matchName(url);
        Path path = Path.of(Objects.requireNonNull(this.getClass().getResource("/static" + fileName)).getPath());
        String body = Files.readString(path);

        ContentFormat contentFormat = ContentFormat.findByExtension(url);

        return new ResponseBody(body, contentFormat);
    }
}
