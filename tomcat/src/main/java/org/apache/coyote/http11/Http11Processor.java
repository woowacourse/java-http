package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.request.CookieSessionAuthenticator;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = new SessionManager();

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            RequestLine requestLine = RequestLine.from(reader.readLine());
            HttpRequest httpRequest = HttpRequest.of(reader);
            CookieSessionAuthenticator cookieSessionAuthenticator = new CookieSessionAuthenticator(
                    httpRequest,
                    sessionManager
            );
            HttpResponse httpResponse = new HttpResponse(outputStream);

            // 쿠키 검증
            if (httpRequest.containsCookie()) {
                cookieSessionAuthenticator.validateUserCookie();
            }

            // localhost:8080
            if (requestLine.isDefaultPage()) {
                String response = httpResponse.getResponse();

                httpResponse.sendResponse(response);
                return;
            }

            // user login
            if (requestLine.startsWithLogin()) {
                LoginController loginController = new LoginController(sessionManager);
                loginController.service(requestLine, httpRequest, httpResponse);
                return;
            }

            // user register
            if (requestLine.startsWithRegister()) {
                RegisterController registerController = new RegisterController();
                registerController.service(requestLine, httpRequest, httpResponse);
                return;
            }

            httpResponse.sendResponse(httpResponse.getResponseHeader(requestLine.getPath()));
            httpResponse.sendFile(requestLine.getPath());

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
