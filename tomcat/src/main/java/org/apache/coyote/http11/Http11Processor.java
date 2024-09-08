package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Method;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.session.Session;
import org.apache.util.QueryStringParser;
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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            Http11Request request = Http11Request.from(inputStream);
            log.info("http request : {}", request);
            Http11Response response = Http11Response.create();

            handle(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(Http11Request request, Http11Response response) throws IOException {
        try {
            if (request.isStaticResourceRequest()) {
                getView(request.getEndpoint(), response);
                return;
            }
            String endpoint = request.getEndpoint();
            Http11Method method = request.getMethod();
            if (method == Http11Method.GET && "/".equals(endpoint)) {
                response.addBody("Hello world!");
                response.addContentType("text/html");
                return;
            }
            if (method == Http11Method.GET && "/login".equals(endpoint)) {
                if (hasUser(request)) {
                    response.sendRedirect("/index.html");
                    return;
                }
                getView("/login.html", response);
                return;
            }
            if (method == Http11Method.GET && "/register".equals(endpoint)) {
                getView("/register.html", response);
                return;
            }
            if (method == Http11Method.POST && "/login".equals(endpoint)) {
                login(request, response);
                return;
            }
            if (method == Http11Method.POST && "/register".equals(endpoint)) {
                register(request, response);
                return;
            }
            response.sendRedirect("/404.html");
        } catch (Exception e) {
            response.sendRedirect("/500.html");
        }
    }

    private boolean hasUser(Http11Request request) {
        try {
            Session session = request.getSession();
            session.getAttribute("user");
            return true;
        } catch (IllegalArgumentException e) {
        }
        return false;
    }

    private void getView(String view, Http11Response response) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + view);
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 자원입니다.");
        }
        Path path = new File(resource.getFile()).toPath();
        String contentType = Files.probeContentType(path);
        response.addBody(new String(Files.readAllBytes(path)));
        response.addContentType(contentType);
    }

    private void login(Http11Request request, Http11Response response) {
        String body = request.getBody();
        Map<String, List<String>> queryStrings = QueryStringParser.parseQueryString(body);
        String account = queryStrings.get("account").getFirst();
        String password = queryStrings.get("password").getFirst();

        InMemoryUserRepository.findByAccount(account)
                .ifPresent((user -> {
                    if (user.checkPassword(password)) {
                        Session session = request.getSession();
                        session.setAttribute("user", user);
                        response.addCookie("JSESSIONID", session.getId());
                        response.sendRedirect("/index.html");
                        return;
                    }
                    response.sendRedirect("/401.html");
                }));
    }

    private void register(Http11Request request, Http11Response response) {
        String body = request.getBody();
        Map<String, List<String>> queryStrings = QueryStringParser.parseQueryString(body);
        String account = queryStrings.get("account").getFirst();
        String email = queryStrings.get("email").getFirst();
        String password = queryStrings.get("password").getFirst();

        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }
}
