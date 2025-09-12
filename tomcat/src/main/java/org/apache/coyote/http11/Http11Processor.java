package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.CookieSessionAuthenticator;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.request.UserRegisterManager;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpCookie httpCookie = new HttpCookie();
    private final SessionManager sessionManager = new SessionManager();

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            RequestLine requestLine = RequestLine.from(reader.readLine());
            HttpRequest httpRequest = HttpRequest.of(reader);
            CookieSessionAuthenticator cookieSessionAuthenticator = new CookieSessionAuthenticator(
                    httpRequest, sessionManager
            );
            HttpResponse httpResponse = new HttpResponse(outputStream);

            if (httpRequest.containsCookie()) {
                cookieSessionAuthenticator.validateUserCookie();
            }

            if (requestLine.isGetMethod()) {
                if (requestLine.isDefaultPage()) {
                    String response = httpResponse.getResponse();

                    httpResponse.sendResponse(response);
                    return;
                }

                httpResponse.sendResponse(httpResponse.getResponse(requestLine.getPath()));
                httpResponse.sendFile(requestLine.getPath());
                return;
            }

            if (requestLine.isPostMethod()){

                String requestBody = httpRequest.getRequestBody();

                if (requestLine.startsWithLogin()) {
                    authenticateUserFromRequestPath(requestBody, httpResponse);
                    serveStaticFile(requestLine.getPath(), httpResponse);
                    return;
                }

                registerUser(requestBody, httpResponse);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void serveStaticFile(Path path, HttpResponse httpResponse) throws IOException {
        if (Files.exists(path)) {
            final var responseHeaders = httpResponse.getResponse(path);
            httpResponse.sendResponse(responseHeaders);
            httpResponse.sendFile(path);
            return;
        }

        String notFoundResponse = httpResponse.getNotFoundResponse(StatusCode.NOT_FOUND);
        httpResponse.sendResponse(notFoundResponse);
    }

    private void registerUser(String requestBody, HttpResponse httpResponse) throws IOException {
        UserRegisterManager userRegisterManager = UserRegisterManager.of(requestBody);

        if (userRegisterManager.existsUserByAccount()) {
            httpResponse.sendResponse(httpResponse.buildRedirectHeaders("/register.html"));
            return;
        }

        userRegisterManager.saveUser();
        httpResponse.sendResponse(httpResponse.buildRedirectHeaders("index.html"));
    }

    private void authenticateUserFromRequestPath(String requestBody, HttpResponse httpResponse) throws IOException {
        UserRegisterManager userRegisterManager = UserRegisterManager.of(requestBody);

        if (userRegisterManager.isExistsUser()) {
            User user = userRegisterManager.getUser();
            log.info("User: account = {}, password = {}", user.getAccount(), user.getPassword());

            if (userRegisterManager.isPasswordCorrect()) {
                String cookieSession = getSession(user);
                httpResponse.sendResponse(httpResponse.buildRedirectHeaders("/index.html", cookieSession));
                return;
            }
        }

        httpResponse.sendResponse(httpResponse.buildRedirectHeaders("/401.html"));
    }

    private String getSession(User user) {
        String cookieSession = httpCookie.getCookieSession();
        Session session = new Session(cookieSession);

        session.setAttribute("user", user);
        sessionManager.add(session);

        return cookieSession;
    }
}
