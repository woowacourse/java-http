package nextstep.jwp;

import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
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

import static nextstep.jwp.AcceptType.TEXT_CSS;
import static nextstep.jwp.Header.ACCEPT;
import static nextstep.jwp.Header.CONTENT_TYPE;

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
            Request request = Request.of(inputStream);
            byte[] response = new byte[0];
            if (request.checkMethod("GET")) {
                response = get(request);
            }

            if (request.checkMethod("POST")) {
                response = post(request);
            }

            outputStream.write(response);
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private byte[] get(Request request) throws IOException {

        if (request.hasHeaderValue(ACCEPT)) {
            String accept = request.getHeaderValue(ACCEPT);
            if (accept.contains(TEXT_CSS)) {
                Map<String, String> responseHeader = new HashMap<>();
                responseHeader.put(CONTENT_TYPE, TEXT_CSS);
                return Response.ok(responseHeader, request.getPath());
            }
        }

        if (request.isMatchedPath("/login")) {
            return Response.ok("/login.html");
        }

        if (request.isMatchedPath("/register")) {
            return Response.ok("/register.html");
        }

        return Response.ok(request.getPath());
    }

    private byte[] post(Request request) throws IOException {
        if (request.isMatchedPath("/login")) {
            try {
                userService.login(request.getQueryValue("account"), request.getQueryValue("password"));
                return Response.redirect302("/index.html");
            } catch (RuntimeException e) {
                return Response.redirect302("/401.html");
            }
        }

        if (request.isMatchedPath("/register")) {
            try {
                userService.save(request.getQueryValue("account"), request.getQueryValue("password"),
                        request.getQueryValue("email"));
                return Response.redirect302("/index.html");
            } catch (RuntimeException e) {
                return Response.redirect302("/401.html");
            }
        }

        throw new RuntimeException();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
