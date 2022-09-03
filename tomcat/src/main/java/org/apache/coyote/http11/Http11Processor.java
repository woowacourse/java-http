package org.apache.coyote.http11;

import static org.apache.coyote.constant.HttpStatus.NOT_FOUND;
import static org.apache.coyote.constant.HttpStatus.OK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.apache.coyote.constant.HttpStatus;
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
            final HttpResponse httpResponse = new HttpResponse();

            doService(httpRequest, httpResponse);

            write(outputStream, httpResponse);

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

    private void doService(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final String requestUri = httpRequest.getPath();
        String viewName = STATIC_PATH + requestUri;

        if (WELCOME_PAGE_PATH.equals(httpRequest.getPath())) {
            renderWelcomePage(httpResponse);
            return;
        } else if (LOGIN_PAGE_PATH.equals(requestUri)) {
            viewName = login(httpRequest);
        }

        render(httpResponse, viewName);
    }

    private void renderWelcomePage(final HttpResponse httpResponse) {
        httpResponse.setBody(WELCOME_MESSAGE);
        httpResponse.setStatus(OK);
    }

    private static String login(final HttpRequest httpRequest) {
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

        return LOGIN_PAGE;
    }

    private void render(final HttpResponse httpResponse, final String viewName) throws IOException {
        URL resource = getClass().getClassLoader().getResource(viewName);
        HttpStatus statusCode = OK;

        if (resource == null) {
            resource = getClass().getClassLoader().getResource(NOT_FOUND_PAGE);
            statusCode = NOT_FOUND;
        }

        httpResponse.setBody(resource);
        httpResponse.setStatus(statusCode);
    }

    private void write(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
