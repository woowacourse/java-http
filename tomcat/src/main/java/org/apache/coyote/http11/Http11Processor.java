package org.apache.coyote.http11;

import static com.techcourse.db.InMemoryUserRepository.findByAccount;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    private void empty(OutputStream outputStream, Request request) throws IOException {
        Response response = Response.empty(request);
        response.response(outputStream);
    }

    private void login(OutputStream outputStream, Request request) throws IOException {
        String account = request.getRequestParam("account");
        String password = request.getRequestParam("password");
        User user = findByAccount(account).orElse(null);
        if (user != null && user.checkPassword(password)) {
            log.info(user.toString());
            loginSuccess(outputStream);
        }
        if ((user != null && !user.checkPassword(password))) {
            loginFail(outputStream);
        }
        Response response = handling(request, StatusCode.OK);
        response.response(outputStream);
    }

    private void loginFail(OutputStream outputStream) throws IOException {
        Response.redirect(outputStream, "/401");
    }

    private void loginSuccess(OutputStream outputStream) throws IOException {
        Response.redirect(outputStream, "/index");
    }

    private void register(OutputStream outputStream, Request request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            Response response = handling(request, StatusCode.OK);
            response.response(outputStream);
            return;
        }
        String account = request.getRequestParam("account");
        String password = request.getRequestParam("password");
        String email = request.getRequestParam("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        Response.redirect(outputStream, "/index");
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
            Request request = HttpParser.getRequest(inputStream);
            log.info("request: {}", request);

            dispatch(request, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void dispatch(Request request, OutputStream outputStream) throws IOException {
        if (request.getPath().equals("/")) {
            empty(outputStream, request);
            return;
        }
        if (request.getPath().startsWith("/login")) {
            login(outputStream, request);
            return;
        }
        if (request.getPath().startsWith("/register")) {
            register(outputStream, request);
            return;
        }
        Response response = handling(request, StatusCode.OK);
        response.response(outputStream);
    }

    private Response handling(Request request, StatusCode statusCode) throws IOException {
        return Response.from(request, statusCode, getClass().getClassLoader());
    }
}
