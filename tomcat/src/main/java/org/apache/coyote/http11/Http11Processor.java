package org.apache.coyote.http11;

import static org.apache.coyote.http11.request.Method.GET;
import static org.apache.coyote.http11.request.Method.POST;
import static org.apache.coyote.http11.response.Status.FOUND;
import static org.apache.coyote.http11.response.Status.NOT_FOUND;
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
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Method;
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

            final Http11Request request = readRequest(inputStream);
            final Http11Response response = makeResponseOf(request);

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Request readRequest(final InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final Http11Request request = readRequestHeader(bufferedReader);
        final String contentLength = request.getHeader("Content-Length");
        if (contentLength != null) {
            final String requestBody = readRequestBody(bufferedReader, Integer.parseInt(contentLength));
            request.setBody(requestBody);
        }
        return request;
    }

    private Http11Request readRequestHeader(final BufferedReader bufferedReader) {
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

        return Http11Request.from(input.toString());
    }

    private String readRequestBody(final BufferedReader bufferedReader, final int contentLength) {
        final char[] buffer = new char[contentLength];
        try {
            bufferedReader.read(buffer, 0, contentLength);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return new String(buffer);
    }

    private Http11Response makeResponseOf(final Http11Request request) {
        final Method method = request.getMethod();
        final String path = request.getPath();
        Status status;
        final Http11Response response;

        if (method == GET) {
            status = OK;
            final String responseBody = getResponseBodyFromResource(OK, path);
            response = new Http11Response(status, responseBody);
            final String accept = request.getHeader("Accept");
            if (accept != null && accept.contains("css")) {
                response.addHeader("Content-Type", "text/css;charset=utf-8 ");
            }
            return response;
        }
        if (method == POST) {
            status = NOT_FOUND;
            if (path.equals("/login")) {
                status = logIn(request);
            }
            if (path.equals("/register")) {
                status = register(request);
            }

            final String responseBody = getResponseBodyFromResource(status, path);

            response = new Http11Response(status, responseBody);
            if (status == FOUND) {
                response.addHeader("Location", "/index.html");
            }
            return response;
        }
        throw new IllegalArgumentException("Invalid Request Uri");
    }

    private Status logIn(final Http11Request request) {
        final Map<String, String> bodyFields = parseFormData(request.getBody());
        final String account = bodyFields.get("account");
        final String password = bodyFields.get("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (user.checkPassword(password)) {
            log.info(user.toString());
            return FOUND;
        }

        return UNAUTHORIZED;
    }

    private Status register(final Http11Request request) {
        final Map<String, String> bodyFields = parseFormData(request.getBody());

        final String account = bodyFields.get("account");
        final String password = bodyFields.get("password");
        final String email = bodyFields.get("email");

        final User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        return FOUND;
    }

    private Map<String, String> parseFormData(final String body) {
        final Map<String, String> fields = new HashMap<>();

        for (final String field : body.split("&")) {
            final String[] param = field.split("=", 2);
            final String name = param[0];
            final String value = param[1];
            fields.put(name, value);
        }
        return fields;
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
