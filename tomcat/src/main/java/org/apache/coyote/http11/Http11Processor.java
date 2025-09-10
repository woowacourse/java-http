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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.cookie.SessionCookieFactory;
import org.apache.coyote.http11.exception.HttpStatusException;
import org.apache.coyote.http11.request.FormUrlEncodedHttpRequestParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.startline.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseFactory;
import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.ResponseHeader;
import org.apache.coyote.http11.response.startline.HttpStatusCode;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.session.SessionParser;
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
            final HttpRequest httpRequest = HttpRequest.of(new FormUrlEncodedHttpRequestParser(reader));
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
            final HttpResponse errorResponse = HttpResponseFactory.createStaticHttpResponse(statusCode,
                    "/" + statusCode.getStatusCode() + ".html");
            sendHttpResponse(errorResponse, outputStream);
        }
    }

    private HttpResponse handleWelcomePage() {
        final ResponseBody responseBody = ResponseBody.createPlainTextResponseBody("Hello world!");
        final ResponseHeader contentTypeHeader = ResponseHeader.createContentTypeHeader(responseBody);
        final ResponseHeader contentLengthHeader = ResponseHeader.createContentLength(responseBody);

        return HttpResponseFactory.createHttpResponse(
                HttpStatusCode.OK,
                List.of(contentTypeHeader, contentLengthHeader),
                responseBody
        );
    }

    private HttpResponse handleRegisterGetRequest() throws IOException {
        return HttpResponseFactory.createStaticHttpResponse(HttpStatusCode.OK, "/register.html");
    }

    private HttpResponse handleRegisterPostRequest(final HttpRequest httpRequest) {
        final String account = httpRequest.getBodyParameter("account");
        final String password = httpRequest.getBodyParameter("password");
        final String email = httpRequest.getBodyParameter("email");
        userService.signup(account, password, email);

        return HttpResponseFactory.createRedirectHttpResponse("/index.html");
    }

    private HttpResponse handleStaticResourceGetRequest(final HttpRequest httpRequest) throws IOException {
        return HttpResponseFactory.createStaticHttpResponse(HttpStatusCode.OK, httpRequest.getStaticResourcePath());
    }

    private HttpResponse handleLoginGetRequest(final HttpRequest httpRequest) throws IOException {
        final Optional<Session> session = SessionParser.extractSessionFromRequest(httpRequest);
        if (session.isPresent()) {
            final User loginUser = (User) session.get().getAttribute("user");
            if (loginUser != null) {
                return HttpResponseFactory.createRedirectHttpResponse("/index.html");
            }
        }

        return HttpResponseFactory.createStaticHttpResponse(HttpStatusCode.OK, "/login.html");
    }

    private HttpResponse handleLoginPostRequest(final HttpRequest httpRequest) throws IOException {
        final String account = httpRequest.getBodyParameter("account");
        final String password = httpRequest.getBodyParameter("password");
        final Optional<User> user = userService.login(account, password);

        if (user.isEmpty()) {
            final HttpStatusCode statusCode = HttpStatusCode.UNAUTHORIZED;
            return HttpResponseFactory.createStaticHttpResponse(statusCode, "/" + statusCode.getStatusCode() + ".html");
        }

        log.info("user: " + user.get());
        final Session session = sessionManager.createAndSaveSession(Map.of("user", user.get()));
        final HttpCookie sessionCookie = SessionCookieFactory.createSessionCookie(session);
        final HttpResponse response = HttpResponseFactory.createRedirectHttpResponse("/index.html");
        response.setCookie(sessionCookie);
        return response;
    }

    private void sendHttpResponse(final HttpResponse response, final OutputStream outputStream) throws IOException {
        final String parsedResponse = response.toResponseText();
        outputStream.write(parsedResponse.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
