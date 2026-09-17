package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParams;
import org.apache.coyote.http11.resolver.ContentType;
import org.apache.coyote.http11.resolver.PageResolver;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
        try (connection;
             final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.from(reader);
            HttpResponse response = null;
            if (request.getMethod() == HttpMethod.POST && request.getUri().contains("login")) {
                response = handleLogin(request);
            }
            else if (request.getMethod() == HttpMethod.GET && request.getUri().equals("/logout")) {
                response = handleLogout(request);
            }
            else if (request.getMethod() == HttpMethod.POST && request.getUri().contains("register")) {
                response = handleRegister(request);
            }
            else if (request.getMethod() == HttpMethod.GET && request.getUri().equals("/login")) {
                response = handleLoginPage(request);
            }
            else if (request.getMethod() == HttpMethod.GET) {
                String resourcePath = PageResolver.resolve(request.getUri());
                response = handlePage(resourcePath);
            }

            if (response != null) {
                ensureSessionCookie(request, response);
                outputStream.write(response.getResponseBytes());
            }
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleLogout(HttpRequest request) {
        Session session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        HttpResponse response = new HttpResponse();
        response.setHeader(new HttpResponseHeader(new Cookies(), new LinkedHashMap<>()));
        response.addCookie(Cookie.expiredJSessionId());
        response.sendRedirect("/index.html");
        return response;
    }

    private HttpResponse handleLoginPage(HttpRequest request) throws IOException, URISyntaxException {
        Session session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            HttpResponse response = new HttpResponse();
            response.setHeader(new HttpResponseHeader(new Cookies(), new LinkedHashMap<>()));
            response.sendRedirect("/index.html");
            return response;
        }
        return handlePage(PageResolver.resolve(request.getUri()));
    }

    private void ensureSessionCookie(HttpRequest request, HttpResponse response) throws IOException {
        if (response.hasCookie("JSESSIONID")) {
            return;
        }
        String sessionId = request.getHeader().getCookieValue("JSESSIONID");
        if (sessionId == null) {
            Session session = request.getSession(true);
            response.addCookie(Cookie.ofJSessionId(session.getId()));
        }
    }

    private HttpResponse handleRegister(HttpRequest request) {
        QueryParams params = request.getBody().getQueryParams();
        String account = params.getValue("account");
        String password = params.getValue("password");
        String email = params.getValue("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        HttpResponse response = new HttpResponse();
        response.setHeader(new HttpResponseHeader(new Cookies(), new LinkedHashMap<>()));
        response.sendRedirect("/index.html");
        return response;
    }

    private HttpResponse handlePage(String resourcePath) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        if (resource == null) {
            throw new IllegalStateException("resource not found: " + resourcePath);
        }
        Path path = Path.of(resource.toURI());
        byte[] bytes = Files.readAllBytes(path);

        HttpResponse response = new HttpResponse();
        response.setBody(new HttpResponseBody(bytes));

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", ContentType.from(resourcePath));
        headers.put("Content-Length", String.valueOf(bytes.length));
        response.setHeader(new HttpResponseHeader(new Cookies(), headers));

        return response;
    }

    private HttpResponse handleLogin(HttpRequest request) throws IOException {
        QueryParams params = request.getBody().getQueryParams();
        String account = params.getValue("account");
        String password = params.getValue("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        HttpResponse response = new HttpResponse();
        response.setHeader(new HttpResponseHeader(new Cookies(), new LinkedHashMap<>()));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                final var session = request.getSession(true);
                session.setAttribute("user", user);
                response.addCookie(Cookie.ofJSessionId(session.getId()));
                response.sendRedirect("/index.html");
                return response;
            }
        }
        response.sendRedirect("/401.html");
        return response;
    }
}
