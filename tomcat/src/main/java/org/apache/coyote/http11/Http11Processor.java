package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
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

            // GET /
            if (httpRequest.isPathEqualsTo("/")) {
                final HttpResponse response = HttpResponseParser.createWelcomeHttpResponse();
                sendHttpResponse(response, outputStream);
                return;
            }

            // GET /register
            if (httpRequest.isPathEqualsTo("/register") && httpRequest.getHttpMethod() == HttpMethod.GET) {
                final HttpResponse response = HttpResponseParser.parseToHttpResponse(HttpStatusCode.OK,
                        "/register.html");
                sendHttpResponse(response, outputStream);
                return;
            }

            // POST /register
            if (httpRequest.isPathEqualsTo("/register") && httpRequest.getHttpMethod() == HttpMethod.POST) {
                final String account = httpRequest.getBodyParameter("account");
                final String password = httpRequest.getBodyParameter("password");
                final String email = httpRequest.getBodyParameter("email");

                userService.signup(account, password, email);
                final HttpResponse response = HttpResponseParser.parseToRedirectHttpResponse("/index.html");
                sendHttpResponse(response, outputStream);
                return;
            }

            // GET /login
            if (httpRequest.isPathEqualsTo("/login") && httpRequest.getHttpMethod() == HttpMethod.GET) {
                final HttpResponse response = HttpResponseParser.parseToHttpResponse(HttpStatusCode.OK, "/login.html");
                sendHttpResponse(response, outputStream);
                return;
            }

            // POST /login
            if (httpRequest.isPathEqualsTo("/login") && httpRequest.getHttpMethod() == HttpMethod.POST) {
                final String account = httpRequest.getBodyParameter("account");
                final String password = httpRequest.getBodyParameter("password");
                final Optional<User> user = InMemoryUserRepository.findByAccount(account);

                if (user.isEmpty() || !user.get().checkPassword(password)) {
                    final HttpResponse errorResponse = HttpResponseParser.parseToErrorResponse(
                            HttpStatusCode.UNAUTHORIZED);
                    sendHttpResponse(errorResponse, outputStream);
                }

                final HttpResponse response = HttpResponseParser.parseToRedirectHttpResponse("/index.html");
                sendHttpResponse(response, outputStream);
                log.info("user: " + user);
                return;
            }

            // 이 외의 정적 요청
            final HttpResponse response = HttpResponseParser.parseToHttpResponse(HttpStatusCode.OK,
                    httpRequest.getStaticResourcePath());
            sendHttpResponse(response, outputStream);

        } catch (HttpStatusException e) {
            final HttpStatusCode statusCode = e.getStatusCode();
            final HttpResponse errorResponse = HttpResponseParser.parseToErrorResponse(statusCode);
            sendHttpResponse(errorResponse, outputStream);
        }
    }

    private void sendHttpResponse(final HttpResponse response, final OutputStream outputStream) throws IOException {
        final String parsedResponse = response.toResponseText();
        outputStream.write(parsedResponse.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
