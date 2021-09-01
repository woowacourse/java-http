package nextstep.jwp;

import nextstep.jwp.exception.BaseException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static nextstep.jwp.http.AcceptType.TEXT_CSS;
import static nextstep.jwp.http.Header.ACCEPT;
import static nextstep.jwp.http.Header.CONTENT_TYPE;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    private final UserService userService;

    public RequestHandler(Socket connection, UserService userService) {
        this.connection = Objects.requireNonNull(connection);
        this.userService = userService;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpRequest.of(inputStream);
            byte[] response = new byte[0];

            if (httpRequest.checkMethod("GET")) {
                response = get(httpRequest);
            }

            if (httpRequest.checkMethod("POST")) {
                response = post(httpRequest);
            }

            outputStream.write(response);
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private byte[] get(HttpRequest httpRequest) throws IOException {
        if (checkCssRequest(httpRequest)) {
            Map<String, String> responseHeader = new HashMap<>();
            responseHeader.put(CONTENT_TYPE, TEXT_CSS);
            return HttpResponse.ok(responseHeader, httpRequest.getPath());
        }

        if (httpRequest.getPath().equals("/")) {
            return HttpResponse.ok("Hello world!".getBytes());
        }

        if (httpRequest.isMatchedPath("/login")) {
            return HttpResponse.ok("/login.html");
        }

        if (httpRequest.isMatchedPath("/register")) {
            return HttpResponse.ok("/register.html");
        }

        return HttpResponse.ok(httpRequest.getPath());
    }

    private byte[] post(HttpRequest httpRequest) {
        if (httpRequest.isMatchedPath("/login")) {
            return login(httpRequest);
        }

        if (httpRequest.isMatchedPath("/register")) {
            return signUp(httpRequest);
        }

        return HttpResponse.notFound();
    }

    private byte[] login(HttpRequest httpRequest) {
        try {
            userService.login(httpRequest.getQueryValue("account"), httpRequest.getQueryValue("password"));
            return HttpResponse.redirect302("/index.html");
        } catch (BaseException e) {
            return HttpResponse.redirect302("/401.html");
        }
    }

    private byte[] signUp(HttpRequest httpRequest) {
        try {
            userService.save(httpRequest.getQueryValue("account"), httpRequest.getQueryValue("password"),
                    httpRequest.getQueryValue("email"));
            return HttpResponse.redirect302("/index.html");
        } catch (BaseException e) {
            return HttpResponse.redirect302("/401.html");
        }
    }

    private boolean checkCssRequest(HttpRequest httpRequest) {
        if (httpRequest.hasHeaderValue(ACCEPT)) {
            String accept = httpRequest.getHeaderValue(ACCEPT);
            return accept.contains(TEXT_CSS);
        }
        return false;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
