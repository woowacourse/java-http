package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String WELCOME_MESSAGE = "Hello world!";

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
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest httpRequest = HttpRequest.of(bufferedReader.readLine(), getHeaders(bufferedReader));

            final int contentLength = httpRequest.getContentLength();
            final Map<String, String> requestBody = getRequestBody(bufferedReader, contentLength);

            HttpResponse httpResponse = HttpResponse.fromStatusCode(200)
                    .setResponseBody(getResponseBody(httpRequest.getPath()))
                    .setContentType(httpRequest.getContentType());

            if (httpRequest.isRegister()) {
                httpResponse = register(requestBody, httpResponse);
            }

            if (httpRequest.isLogin()) {
                httpResponse = login(requestBody, httpResponse);
            }

            if (httpRequest.isLoginPage() && httpRequest.alreadyLogin()) {
                httpResponse = httpResponse.changeStatusCode(302)
                        .setLocationAsHome();
            }

            outputStream.write(httpResponse.toResponseBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        String header;
        while (!"".equals((header = bufferedReader.readLine()))) {
            final String[] splitHeader = header.split(": ");
            headers.put(splitHeader[0], splitHeader[1]);
        }

        return headers;
    }

    private Map<String, String> getRequestBody(final BufferedReader bufferedReader, final int contentLength)
            throws IOException {
        if (contentLength == 0) {
            return Collections.emptyMap();
        }
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);

        return Arrays.stream(requestBody.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1], (a, b) -> b));
    }

    private String getResponseBody(final String path) throws IOException {
        String responseBody = WELCOME_MESSAGE;

        if (!path.equals("/")) {
            String resourcePath = "static/" + path;
            if (!resourcePath.contains(".")) {
                resourcePath += ".html";
            }

            final String resource = getClass().getClassLoader()
                    .getResource(resourcePath)
                    .getPath();
            final File file = new File(resource);
            final BufferedReader fileReader = new BufferedReader(new FileReader(file));
            responseBody = fileReader.lines()
                    .collect(Collectors.joining("\n"));
            responseBody += "\n";

            fileReader.close();
        }

        return responseBody;
    }

    private HttpResponse login(final Map<String, String> requestBody, final HttpResponse httpResponse) {
        final Optional<User> possibleUser = InMemoryUserRepository.findByAccount(requestBody.get("account"));
        if (possibleUser.isEmpty()) {
            return httpResponse;
        }

        final UUID sessionId = UUID.randomUUID();
        final HttpSession session = new Session(String.valueOf(sessionId));
        session.setAttribute("user", possibleUser.get());
        new SessionManager().add(session);

        return httpResponse.changeStatusCode(302)
                .setLocationAsHome()
                .setSessionId(sessionId);
    }

    private HttpResponse register(final Map<String, String> requestBody, HttpResponse httpResponse) {
        final User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        final UUID sessionId = UUID.randomUUID();
        final HttpSession session = new Session(String.valueOf(sessionId));
        session.setAttribute("user", user);
        new SessionManager().add(session);

        httpResponse = httpResponse.changeStatusCode(302)
                .setLocationAsHome()
                .setSessionId(sessionId);
        return httpResponse;
    }
}
