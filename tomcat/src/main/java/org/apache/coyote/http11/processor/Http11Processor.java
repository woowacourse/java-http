package org.apache.coyote.http11.processor;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.http11.parser.HttpRequestParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.session.HttpSession;
import org.apache.coyote.session.HttpSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpSessionManager SESSION_MANAGER = new HttpSessionManager();
    private static final String OK = "200 OK";
    private static final String FOUND = "302 Found";
    private static final String BAD_REQUEST = "400 Bad Request";
    private static final String UNAUTHORIZED = "401 Unauthorized";
    private static final String NOT_FOUND = "404 Not Found";
    private static final String INTERNAL_SERVER_ERROR = "500 Internal Server Error";

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

            Optional<HttpRequest> request = HttpRequestParser.parse(inputStream);
            if (request.isEmpty()) {
                return;
            }

            HttpResponse httpResponse = new HttpResponse(outputStream);

            if (request.get().getUrl().equals("/")) {
                sendDefaultResource(httpResponse);
                return;
            }

            if (request.get().getMethod().equals("GET") && request.get().getUrl().equals("/login")){
                Optional<HttpCookie> httpCookie = request.get().getCookie("JSESSIONID");

                if (httpCookie.isPresent() && SESSION_MANAGER.containsKey(httpCookie.get().getValue())) {
                    sendRedirectResponse(httpResponse, "/index.html", FOUND);
                } else {
                    sendStaticFile(httpResponse, "static/login.html", OK);
                }
                return;
            }

            if (request.get().getMethod().equals("GET") && !request.get().getUrl().contains("?")) {
                String staticUrl = "static" + request.get().getUrl();
                if (!request.get().getUrl().contains(".")) {
                    staticUrl += ".html";
                }
                sendStaticFile(httpResponse, staticUrl, OK);
                return;
            }

            if (request.get().getMethod().equals("POST")&& request.get().getUrl().equals("/login")) {
                sendLoginUserResponse(request.get(), httpResponse);
                return;
            }

            if (request.get().getMethod().equals("POST") && request.get().getUrl().equals("/register")) {
                sendRegisterUserResponse(request.get(), httpResponse);
                return;
            }
            sendStaticFile(httpResponse, "static/404.html", NOT_FOUND);

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendDefaultResource(HttpResponse response) throws IOException {
        final var responseBody = "Hello world!";
        response.setStatusCode(OK)
                .setContentType("text/html;charset=utf-8")
                .setBody(responseBody)
                .send();
    }

    private void sendStaticFile(HttpResponse response, String uri, String statusCode) throws IOException, URISyntaxException {
        final var path = Paths.get(findUri(uri));
        final var contentType = Files.probeContentType(path);
        final byte[] responseBodyBytes = Files.readAllBytes(path);

        response.setStatusCode(statusCode)
                .setContentType(contentType + ";charset=utf-8")
                .setBody(responseBodyBytes)
                .send();
    }

    private void sendRedirectResponse(HttpResponse response, String location, String statusCode) throws IOException {
        response.setStatusCode(statusCode)
                .setLocation(location);

        response.send();
    }

    private void sendLoginUserResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException {
        String account = httpRequest.getParameter("account").orElse(null);
        String password = httpRequest.getParameter("password").orElse(null);
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            sendStaticFile(httpResponse,"static/401.html", UNAUTHORIZED);
        }
        if (user.get().checkPassword(password)) {
            log.info(user.toString());

            Optional<HttpCookie> httpCookie = httpRequest.getCookie("JSESSIONID");
            String jsessionid = httpCookie.map(HttpCookie::getValue).orElseGet(() -> UUID.randomUUID().toString());
            HttpSession session = new HttpSession(jsessionid);
            session.setAttribute("user", user);
            SESSION_MANAGER.add(session);
            httpResponse.setCookie(new HttpCookie("JSESSIONID", jsessionid));

            sendRedirectResponse(httpResponse,"/index.html", FOUND);
        }

        sendRedirectResponse(httpResponse,"static/401.html", UNAUTHORIZED);
    }

    private void sendRegisterUserResponse(HttpRequest request, HttpResponse httpResponse) throws IOException, URISyntaxException {
        String account = request.getParameter("account").orElse(null);
        String password = request.getParameter("password").orElse(null);
        String email = request.getParameter("email").orElse(null);

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            sendStaticFile(httpResponse,"static/register.html", BAD_REQUEST);
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        sendStaticFile(httpResponse,"static/index.html", OK);
    }

    private URI findUri(String staticUrl) throws URISyntaxException {
        final var resource = getClass().getClassLoader().getResource(staticUrl);

        if (resource == null) {
            return Objects.requireNonNull(getClass().getClassLoader().getResource("static/404.html")).toURI();
        }
        return resource.toURI();
    }
}
