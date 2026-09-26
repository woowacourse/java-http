package org.apache.coyote.http11;

import static com.techcourse.db.InMemoryUserRepository.findByAccount;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
            HttpRequest httpRequest = HttpParser.getRequest(inputStream);
            log.info("request: {}", httpRequest);

            dispatch(httpRequest, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void dispatch(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        if (httpRequest.getPath().equals("/")) {
            empty(outputStream, httpRequest);
            return;
        }
        if (httpRequest.getPath().startsWith("/login")) {
            login(outputStream, httpRequest);
            return;
        }
        if (httpRequest.getPath().startsWith("/register")) {
            register(outputStream, httpRequest);
            return;
        }
        HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.OK);
        httpResponse.respond(outputStream);
    }

    private HttpResponse handling(HttpRequest httpRequest, HttpStatusCode httpStatusCode) throws IOException {
        return HttpResponse.from(httpRequest, httpStatusCode, getClass().getClassLoader());
    }

    private void empty(OutputStream outputStream, HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = HttpResponse.empty(httpRequest);
        httpResponse.respond(outputStream);
    }

    private void login(OutputStream outputStream, HttpRequest httpRequest) throws IOException {
        if (httpRequest.getMethod().equals(HttpMethod.GET)) {
            loginGet(outputStream, httpRequest);
        }
        if (httpRequest.getMethod().equals(HttpMethod.POST)) {
            loginPost(outputStream, httpRequest);
        }
    }

    private void loginGet(OutputStream outputStream, HttpRequest httpRequest) throws IOException {
        if (SessionManager.getInstance().hasUser(httpRequest.getJSessionId())) {
            HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.FOUND);
            httpResponse.redirect(outputStream, "/index.html");
            return;
        }
        HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.OK);
        httpResponse.respond(outputStream);
    }

    private void loginPost(OutputStream outputStream, HttpRequest httpRequest) throws IOException {
        String account = httpRequest.getRequestParam("account");
        String password = httpRequest.getRequestParam("password");
        if (account.isEmpty() || password.isEmpty()) {
            loginFail(outputStream, httpRequest);
        }
        User user = findByAccount(account).orElse(null);
        if (user != null && user.checkPassword(password)) {
            loginSuccess(outputStream, httpRequest, user);
        }
        if (user != null && !user.checkPassword(password)) {
            loginFail(outputStream, httpRequest);
        }
        if (!account.isEmpty() && user == null) {
            loginFail(outputStream, httpRequest);
        }

    }

    private void loginFail(OutputStream outputStream, HttpRequest httpRequest) throws IOException {
        log.info("login fail");
        HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.FOUND);
        httpResponse.redirect(outputStream, "/401.html");
    }

    private void loginSuccess(OutputStream outputStream, HttpRequest httpRequest, User user) throws IOException {
        log.info(user.toString());
        final var session = httpRequest.getSession(true);
        session.setAttribute("user", user);
        HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.FOUND);
        httpResponse.redirect(outputStream, "/index.html");
    }

    private void register(OutputStream outputStream, HttpRequest httpRequest) throws IOException {
        if (httpRequest.getMethod() == HttpMethod.GET) {
            HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.OK);
            httpResponse.respond(outputStream);
            return;
        }
        String account = httpRequest.getRequestParam("account");
        String password = httpRequest.getRequestParam("password");
        String email = httpRequest.getRequestParam("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.FOUND);
        httpResponse.redirect(outputStream, "/index.html");
    }
}
