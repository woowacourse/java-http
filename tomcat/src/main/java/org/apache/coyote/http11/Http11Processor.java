package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestCookie;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.Response;
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
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            Request request = new Request(reader);
            Response response = new Response();
            execute(request, response);

            outputStream.write(response.buildHttpMessage().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void execute(Request request, Response response) throws IOException, URISyntaxException {
        log.info("request path = {}", request.getPath());
        if (request.hasPath("/") && request.hasMethod(RequestMethod.GET)) {
            getWelcome(response);
            return;
        }
        if (request.hasPath("/login") && request.hasMethod(RequestMethod.GET)) {
            getLogin(request, response);
            return;
        }
        if (request.hasPath("/login") && request.hasMethod(RequestMethod.POST)) {
            postLogin(request, response);
            return;
        }
        if (request.hasPath("/register") && request.hasMethod(RequestMethod.POST)) {
            postRegister(request, response);
            return;
        }
        showStaticResource(request, response);
    }

    private void getWelcome(Response response) throws URISyntaxException, IOException {
        response.addFileBody("/default.html");
    }

    private void getLogin(Request request, Response response) throws URISyntaxException, IOException {
        response.addFileBody("/login.html");
        Optional<RequestCookie> loginCookie = request.getLoginCookie();
        if (loginCookie.isPresent()) {
            RequestCookie cookie = loginCookie.get();
            boolean isLogin = sessionManager.contains(cookie.getValue());
            if (isLogin) {
                response.sendRedirection("/index.html");
            }
        }
    }

    private void postLogin(Request request, Response response) throws IOException, URISyntaxException {
        RequestBody requestBody = request.getBody();
        Map<String, String> userInfo = requestBody.parseQuery();
        String account = userInfo.getOrDefault("account", "");
        String password = userInfo.getOrDefault("password", "");

        Optional<User> rawUser = InMemoryUserRepository.findByAccount(account);
        if (rawUser.isEmpty() || !rawUser.get().checkPassword(password)) {
            response.setStatusUnauthorized();
            response.addFileBody("/401.html");
            return;
        }
        User user = rawUser.get();
        log.info("user: {}", user);
        String newSessionId = sessionManager.create("user", user);
        response.addLoginCookie(newSessionId);
        response.sendRedirection("/index.html");
    }

    private void postRegister(Request request, Response response) {
        RequestBody requestBody = request.getBody();
        Map<String, String> userInfo = requestBody.parseQuery();
        String account = userInfo.get("account");
        String email = userInfo.get("email");
        String password = userInfo.get("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.sendRedirection("/index.html");
    }

    private void showStaticResource(Request request, Response response) throws URISyntaxException, IOException {
        String requestPath = request.getPath();
        if (requestPath.contains(".")) {
            response.addFileBody(requestPath);
            return;
        }
        response.addFileBody(requestPath + ".html");
    }
}
