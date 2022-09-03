package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String WELCOME_PAGE_PATH = "/";
    private static final String LOGIN_PAGE_PATH = "/login";
    private static final String STATIC_PATH = "static";

    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String LOGIN_PAGE = "static/login.html";

    private static final String WELCOME_MESSAGE = "Hello world!";

    private static final String ACCOUNT_PARAM = "account";
    private static final String PASSWORD_PARAM = "password";

    private static final String OK = "200 OK";
    private static final String NOT_FOUND = "404 Not Found";

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
        try (final InputStream inputStream = connection.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = toHttpRequest(bufferedReader);

            doService(outputStream, httpRequest);

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest toHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final List<String> rawHttpRequest = readHttpRequest(bufferedReader);
        return HttpRequest.from(rawHttpRequest);
    }

    private List<String> readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final List<String> rawHttpRequest = new ArrayList<>();

        String line = " ";
        while (!line.isEmpty()) {
            line = bufferedReader.readLine();
            rawHttpRequest.add(line);
        }

        log.info("============= HTTP REQUEST =============");
        log.info(String.join("\n", rawHttpRequest));

        return rawHttpRequest;
    }

    private void doService(final OutputStream outputStream, final HttpRequest httpRequest) throws IOException {
        final String requestUri = httpRequest.getPath();
        String viewName = STATIC_PATH + requestUri;

        if (WELCOME_PAGE_PATH.equals(httpRequest.getPath())) {
            renderWelcomePage(outputStream);
            return;
        } else if (LOGIN_PAGE_PATH.equals(requestUri)) {
            viewName = LOGIN_PAGE;

            if (httpRequest.haveParam(ACCOUNT_PARAM) && httpRequest.haveParam(PASSWORD_PARAM)) {
                final String account = httpRequest.getParam(ACCOUNT_PARAM);
                final String password = httpRequest.getParam(PASSWORD_PARAM);
                final User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(NoSuchElementException::new);
                if (user.checkPassword(password)) {
                    final String outputMessage = user.toString();
                    log.info(outputMessage);
                }
            }
        }

        render(outputStream, viewName);
    }

    private void renderWelcomePage(final OutputStream outputStream) throws IOException {
        final String responseBody = WELCOME_MESSAGE;
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void render(final OutputStream outputStream, final String viewName) throws IOException {
        URL resource = getClass().getClassLoader().getResource(viewName);
        String statusCode = OK;
        if (resource == null) {
            resource = getClass().getClassLoader().getResource(NOT_FOUND_PAGE);
            statusCode = NOT_FOUND;
        }

        final String response = toResponse(resource, statusCode);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String toResponse(final URL resource, final String statusCode) throws IOException {
        final File file = new File(resource.getFile());
        final Path path = file.toPath();
        final byte[] responseBody = Files.readAllBytes(path);

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + Files.probeContentType(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.length + " ",
                "",
                new String(responseBody));
    }
}
