package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.NotCorrectPasswordException;
import org.apache.coyote.http11.exception.NotFoundAccountException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String ROOT_RESOURCE = "Hello world!";
    private static final String STATIC_PATH = "static";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String LOGIN_PATH = "/login.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String INTERNAL_SERVER_PATH = "/500.html";

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

            final HttpRequest httpRequest = HttpRequest.from(inputStream);
            final HttpResponse response =getRespond(httpRequest);

            outputStream.write(response.toResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getRespond(final HttpRequest httpRequest) {
        try {
            return getResponse(httpRequest);
        } catch (NotFoundAccountException | NotCorrectPasswordException e) {
            return foundResponse(UNAUTHORIZED_PATH);
        } catch (Exception e) {
            return foundResponse(INTERNAL_SERVER_PATH);
        }
    }

    public HttpResponse getResponse(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getRequestURL().getUrl().equals(ROOT_PATH)) {
            return okResponse(httpRequest, ROOT_RESOURCE);
        }
        final String requestUrl = httpRequest.getRequestURL().getAbsolutePath();
        final String responseBody = Resource.readResource(STATIC_PATH + requestUrl);
        if (requestUrl.equals(LOGIN_PATH)) {
            return renderLogin(httpRequest);
        }
        return okResponse(httpRequest, responseBody);
    }

    private HttpResponse renderLogin(final HttpRequest httpRequest) throws IOException {
        final Map<String, String> requestParam = httpRequest.getRequestURL().getRequestParam();
        final String url = httpRequest.getRequestURL().getAbsolutePath();
        if (!requestParam.isEmpty()) {
            return login(requestParam);
        }

        return okResponse(httpRequest, Resource.readResource(STATIC_PATH + url));
    }

    private HttpResponse login(final Map<String, String> requestParam) {
        final User user = InMemoryUserRepository.findByAccount(requestParam.get(ACCOUNT))
                .orElseThrow(NotFoundAccountException::new);

        if (!user.checkPassword(requestParam.get(PASSWORD))) {
            throw new NotCorrectPasswordException();
        }
        log.info(user.toString());
        return foundResponse("/index.html");
    }

    private HttpResponse okResponse(final HttpRequest httpRequest, final String responseBody) {
        final Map<String, String> responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Content-Type", HttpContentType.valueOfCotentType(httpRequest.getRequestURL().getExtension()).getContentType());
        if (!responseBody.isBlank()) {
            responseHeaders.put("Content-Length", responseBody.getBytes().length +" ");
        }

        return new HttpResponse(HttpStatus.OK, responseHeaders, responseBody);
    }

    private HttpResponse foundResponse(final String location) {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Location",location);
        return new HttpResponse(HttpStatus.FOUND, responseHeaders, "");
    }
}
