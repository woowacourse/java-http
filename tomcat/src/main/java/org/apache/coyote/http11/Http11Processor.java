package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.HttpStatusException;
import org.apache.coyote.http11.httprequest.HttpMethod;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;
import org.apache.coyote.http11.parser.HttpRequestParser;
import org.apache.coyote.http11.parser.HttpResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final UserService userService;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.userService = new UserService();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            handleRequest(reader, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(final BufferedReader reader, final OutputStream outputStream) throws IOException {
        try {
            final HttpRequestParser requestParser = new HttpRequestParser(reader);
            final HttpRequest httpRequest = requestParser.readHttpRequest();

            if (httpRequest.matches(HttpMethod.GET, "/")) {
                handleWelcomePage(outputStream);
                return;
            }

            if (httpRequest.matches(HttpMethod.GET, "/register")) {
                handleRegisterGetRequest(outputStream);
                return;
            }

            if (httpRequest.matches(HttpMethod.POST, "/register")) {
                handleRegisterPostRequest(outputStream, httpRequest);
                return;
            }

            if (httpRequest.matches(HttpMethod.GET, "/login")) {
                handleLoginGetRequest(outputStream);
                return;
            }

            if (httpRequest.matches(HttpMethod.POST, "/login")) {
                handleLoginPostRequest(outputStream, httpRequest);
                return;
            }

            handleStaticResourceGetRequest(outputStream, httpRequest);
        } catch (HttpStatusException e) {
            final HttpStatusCode statusCode = e.getStatusCode();
            final HttpResponse errorResponse = HttpResponseParser.parseToErrorResponse(statusCode);
            sendHttpResponse(errorResponse, outputStream);
        }
    }

    private void handleWelcomePage(final OutputStream outputStream) throws IOException {
        final HttpResponse response = HttpResponseParser.createWelcomeHttpResponse();
        sendHttpResponse(response, outputStream);
    }

    private void handleRegisterGetRequest(final OutputStream outputStream) throws IOException {
        final HttpResponse response = HttpResponseParser.parseToHttpResponse(HttpStatusCode.OK,
                "/register.html");
        sendHttpResponse(response, outputStream);
    }

    private void handleRegisterPostRequest(final OutputStream outputStream, final HttpRequest httpRequest)
            throws IOException {
        final String account = httpRequest.getBodyParameter("account");
        final String password = httpRequest.getBodyParameter("password");
        final String email = httpRequest.getBodyParameter("email");
        userService.signup(account, password, email);
        final HttpResponse response = HttpResponseParser.parseToRedirectHttpResponse("/index.html");
        sendHttpResponse(response, outputStream);
    }

    private void handleStaticResourceGetRequest(final OutputStream outputStream, final HttpRequest httpRequest)
            throws IOException {
        final HttpResponse response = HttpResponseParser.parseToHttpResponse(HttpStatusCode.OK,
                httpRequest.getStaticResourcePath());
        sendHttpResponse(response, outputStream);
    }

    private void handleLoginGetRequest(final OutputStream outputStream) throws IOException {
        final HttpResponse response = HttpResponseParser.parseToHttpResponse(HttpStatusCode.OK, "/login.html");
        sendHttpResponse(response, outputStream);
    }

    private void handleLoginPostRequest(final OutputStream outputStream, final HttpRequest httpRequest)
            throws IOException {
        final String account = httpRequest.getBodyParameter("account");
        final String password = httpRequest.getBodyParameter("password");
        Optional<User> user = userService.login(account, password);

        if (user.isEmpty()) {
            final HttpResponse errorResponse = HttpResponseParser.parseToErrorResponse(
                    HttpStatusCode.UNAUTHORIZED);
            sendHttpResponse(errorResponse, outputStream);
            return;
        }

        final HttpResponse response = HttpResponseParser.parseToRedirectHttpResponse("/index.html");
        sendHttpResponse(response, outputStream);
        log.info("user: " + user.get());
    }

    private void sendHttpResponse(final HttpResponse response, final OutputStream outputStream) throws IOException {
        final String parsedResponse = response.toResponseText();
        outputStream.write(parsedResponse.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
