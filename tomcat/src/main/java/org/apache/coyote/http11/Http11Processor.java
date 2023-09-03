package org.apache.coyote.http11;

import static org.apache.coyote.http11.request.Method.GET;
import static org.apache.coyote.http11.response.Status.FOUND;
import static org.apache.coyote.http11.response.Status.OK;
import static org.apache.coyote.http11.response.Status.UNAUTHORIZED;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final String LINE_SEPARATOR = "\r\n";

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

            final Http11Request request = Http11Request.from(readInputStream(inputStream));

            final Http11Response response = makeResponseOf(request);

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readInputStream(final InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final StringBuilder input = new StringBuilder();
        try {
            for (String s = bufferedReader.readLine();
                 !"".equals(s);
                 s = bufferedReader.readLine()) {
                input.append(s);
                input.append(LINE_SEPARATOR);
            }
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }

        return input.toString();
    }


    private Http11Response makeResponseOf(final Http11Request request) {
        if (request.getMethod() == GET) {
            final String path = request.getPath();
            Status status = OK;

            if (path.equals("/login") && request.isQueryParamExist("account", "password")) {
                status = processLogIn(request);
            }

            final String responseBody = getResponseBodyFromResource(status, path);

            final Http11Response response = new Http11Response(status, responseBody);
            final String accept = request.getHeader("Accept");
            if (accept != null && accept.contains("css")) {
                response.addHeader("Content-Type", "text/css;charset=utf-8 ");
            }
            if (status == FOUND) {
                response.addHeader("Location", "/index.html");
            }

            return response;
        }
        throw new IllegalArgumentException("Invalid Request Uri");
    }

    private Status processLogIn(final Http11Request request) {
        final String account = request.getQueryParam("account");
        final String password = request.getQueryParam("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (user.checkPassword(password)) {
            log.info(user.toString());
            return FOUND;
        }

        return UNAUTHORIZED;
    }

    private String getResponseBodyFromResource(final Status status, String path) {
        if (path.equals("/")) {
            return "Hello world!";
        }
        if (path.equals("/favicon.ico")) {
            return "Icon Not Exist";
        }
        if (status.getCode() >= 400) {
            path = "/" + status.getCode();
        }
        if (!path.contains(".")) {
            path += ".html";
        }

        final URL resource = getClass().getClassLoader().getResource("static" + path);

        try {
            final Path filePath = new File(resource.getFile()).toPath();
            return new String(Files.readAllBytes(filePath));

        } catch (final NullPointerException | IOException e) {
            log.error(e.getMessage() + path, e);
            return "Resource Not Exist: " + path;
        }
    }
}
