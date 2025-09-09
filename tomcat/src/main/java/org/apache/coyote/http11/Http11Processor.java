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
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.HttpStatusException;
import org.apache.coyote.http11.httprequest.HttpMethod;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;
import org.apache.coyote.http11.parser.HttpRequestParser;
import org.apache.coyote.http11.parser.HttpResponseParser;
import org.apache.coyote.http11.parser.SessionParser;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final UserService userService;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.userService = new UserService();
        this.sessionManager = SessionManager.getInstance();
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
            HttpResponse response;

            if (httpRequest.matches(HttpMethod.GET, "/")) {
                response = handleWelcomePage();
            } else if (httpRequest.matches(HttpMethod.GET, "/register")) {
                response = handleRegisterGetRequest();
            } else if (httpRequest.matches(HttpMethod.POST, "/register")) {
                response = handleRegisterPostRequest(httpRequest);
            } else if (httpRequest.matches(HttpMethod.GET, "/login")) {
                response = handleLoginGetRequest(httpRequest);
            } else if (httpRequest.matches(HttpMethod.POST, "/login")) {
                response = handleLoginPostRequest(httpRequest);
            } else {
                response = handleStaticResourceGetRequest(httpRequest);
            }

            sendHttpResponse(response, outputStream);
        } catch (HttpStatusException e) {
            final HttpStatusCode statusCode = e.getStatusCode();
            final HttpResponse errorResponse = HttpResponseParser.parseToErrorResponse(statusCode);
            sendHttpResponse(errorResponse, outputStream);
        }
    }

    private HttpResponse handleWelcomePage() {
        return HttpResponseParser.createWelcomeHttpResponse();
    }

    private HttpResponse handleRegisterGetRequest() throws IOException {
        return HttpResponseParser.parseToHttpResponse(HttpStatusCode.OK, "/register.html");
    }

    private HttpResponse handleRegisterPostRequest(final HttpRequest httpRequest) {
        final String account = httpRequest.getBodyParameter("account");
        final String password = httpRequest.getBodyParameter("password");
        final String email = httpRequest.getBodyParameter("email");
        userService.signup(account, password, email);
        return HttpResponseParser.parseToRedirectHttpResponse("/index.html");
    }

    private HttpResponse handleStaticResourceGetRequest(final HttpRequest httpRequest) throws IOException {
        return HttpResponseParser.parseToHttpResponse(HttpStatusCode.OK, httpRequest.getStaticResourcePath());
    }

    private HttpResponse handleLoginGetRequest(final HttpRequest httpRequest) throws IOException {
        final Optional<Session> session = SessionParser.extractSessionFromRequest(httpRequest);
        if (session.isPresent()) {
            final User loginUser = (User) session.get().getAttribute("user");
            if (loginUser != null) {
                return HttpResponseParser.parseToRedirectHttpResponse("/index.html");
            }
        }

        return HttpResponseParser.parseToHttpResponse(HttpStatusCode.OK, "/login.html");
    }

    private HttpResponse handleLoginPostRequest(final HttpRequest httpRequest) throws IOException {
        final String account = httpRequest.getBodyParameter("account");
        final String password = httpRequest.getBodyParameter("password");
        final Optional<User> user = userService.login(account, password);

        if (user.isEmpty()) {
            return HttpResponseParser.parseToErrorResponse(HttpStatusCode.UNAUTHORIZED);
        }

        final Session session = sessionManager.createAndSaveSession(Map.of("user", user.get()));
        log.info("user: " + user.get());
        final HttpResponse response = HttpResponseParser.parseToRedirectHttpResponse("/index.html");
        response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        return response;
    }

    private void sendHttpResponse(final HttpResponse response, final OutputStream outputStream) throws IOException {
        final String parsedResponse = response.toResponseText();
        outputStream.write(parsedResponse.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
