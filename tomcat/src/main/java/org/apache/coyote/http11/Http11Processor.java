package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int QUERY_PARAMETER_PART_COUNT = 2;
    private static final String LOGIN_PATH = "/login";
    private static final String INDEX_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String CRLF = "\r\n";

    private final Socket connection;
    private final ResponseContentResolver responseContentResolver;

    public Http11Processor(final Socket connection) {
        this(connection, new ResponseContentResolver());
    }

    Http11Processor(final Socket connection, final ResponseContentResolver responseContentResolver) {
        this.connection = connection;
        this.responseContentResolver = Objects.requireNonNull(responseContentResolver);
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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final Optional<HttpRequest> parsedRequest;
            try {
                parsedRequest = HttpRequest.readFrom(reader);
            } catch (IOException e) {
                log.warn("Failed to read HTTP request", e);
                return;
            }
            if (parsedRequest.isEmpty()) {
                return;
            }

            final var request = parsedRequest.get();
            final var requestUri = request.uri();
            final String path = requestUri.getPath();
            if (path == null) {
                return;
            }
            final var response = resolveResponse(path, requestUri.getRawQuery());

            try {
                writeResponse(outputStream, response);
            } catch (IOException e) {
                log.warn("Failed to write HTTP response", e);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error("Failed to handle HTTP connection", e);
        }
    }

    private Optional<User> findLoginUser(final String queryString) {
        final Map<String, String> parameters = parseQueryParameters(queryString);
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private Map<String, String> parseQueryParameters(final String queryString) {
        final var nameValuePairs = Arrays.stream(queryString.split("&"))
                .map(parameter -> parameter.split("=", QUERY_PARAMETER_PART_COUNT))
                .toList();
        if (nameValuePairs.stream().anyMatch(pair -> pair.length != QUERY_PARAMETER_PART_COUNT)) {
            return Map.of();
        }

        return nameValuePairs.stream().collect(Collectors.toMap(
                pair -> pair[0],
                pair -> pair[1],
                (previous, replacement) -> replacement));
    }

    private HttpResponse resolveResponse(final String path, final String queryString) {
        if (LOGIN_PATH.equals(path) && queryString != null) {
            return resolveLoginResponse(queryString);
        }
        return resolveStaticResponse(path);
    }

    private HttpResponse resolveLoginResponse(final String queryString) {
        final var loginUser = findLoginUser(queryString);
        if (loginUser.isEmpty()) {
            return HttpResponse.redirect(UNAUTHORIZED_PATH);
        }

        log.info("login user found: {}", loginUser.get().getAccount());
        return HttpResponse.redirect(INDEX_PATH);
    }

    private HttpResponse resolveStaticResponse(final String path) {
        try {
            return HttpResponse.ok(responseContentResolver.resolve(path));
        } catch (HttpException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.error(e.status());
        }
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        final var content = response.content();
        final var headerLines = new ArrayList<String>();
        headerLines.add("HTTP/1.1 " + response.status().code() + " " + response.status().reasonPhrase() + " ");
        for (final var header : response.headers()) {
            headerLines.add(header.name() + ": " + header.value() + " ");
        }
        headerLines.add("Content-Type: " + content.contentType() + " ");
        headerLines.add("Content-Length: " + content.body().length + " ");
        headerLines.add("");
        headerLines.add("");
        final var headers = String.join(CRLF, headerLines);

        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
        outputStream.write(content.body());
        outputStream.flush();
    }
}
