package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
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
             final var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequestParser.parse(inputStream);
            String path = httpRequest.getPath();

            if (path == null) {
                throw new IllegalArgumentException("path is null");
            }

            if (path.equals("/")) {
                processHome(outputStream);
            }
            if (path.startsWith("/login")) {
                processLogin(httpRequest, outputStream);
            }
            if (path.equals("/register")) {
                processRegister(httpRequest, outputStream);
            }
            processFile(outputStream, new HttpResponse(httpRequest.getPath(), httpRequest.getFileType(), HttpStatusCode.OK, null, null));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processHome(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";
        HttpResponse httpResponse = new HttpResponse(null, "html", HttpStatusCode.OK, null, responseBody);
        outputStream.write(httpResponse.toString().getBytes());
        outputStream.flush();
    }

    private void processFile(OutputStream outputStream, HttpResponse httpResponse) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + httpResponse.getPath());
        if (resource == null) {
            return;
        }
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        httpResponse.setResponseBody(responseBody);
        outputStream.write(httpResponse.toString().getBytes());
        outputStream.flush();
    }

    private void processRedirect(OutputStream outputStream, HttpResponse httpResponse) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + httpResponse.getResponseBody());
        if (resource == null) {
            return;
        }
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        httpResponse.setResponseBody(responseBody);
        outputStream.write(httpResponse.toString().getBytes());
        outputStream.flush();
    }

    private void processRegister(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        if (httpRequest.getMethod().equals("GET")) {
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            HttpResponse httpResponse = new HttpResponse(null, "html", HttpStatusCode.OK, null, responseBody);
            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        }

        if (httpRequest.getMethod().equals("POST")) {
            Map<String, List<String>> body = httpRequest.getBody();
            String account = body.get("account").getFirst();
            String password = body.get("password").getFirst();
            String email = body.get("email").getFirst();
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            HttpResponse httpResponse = new HttpResponse("/index.html", "html", HttpStatusCode.FOUND, null, null);
            processRedirect(outputStream, httpResponse);
        }
    }

    private void processLogin(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        if (httpRequest.getMethod().equals("GET")) {
            Map<String, String> cookies = httpRequest.getCookies();
            if (cookies.containsKey("JSESSIONID")) {
                if (sessionManager.findSession(cookies.get("JSESSIONID")) != null) {
                    HttpResponse httpResponse = new HttpResponse("/index.html", "html", HttpStatusCode.FOUND, null, null);
                    processRedirect(outputStream, httpResponse);
                }
            }

            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            HttpResponse httpResponse = new HttpResponse(null, "html", HttpStatusCode.OK, null, responseBody);
            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        }

        if (httpRequest.getMethod().equals("POST")) {
            Map<String, List<String>> body = httpRequest.getBody();
            String account = Optional.ofNullable(body.get("account"))
                    .filter(list -> !list.isEmpty())
                    .map(List::getFirst)
                    .orElseThrow(() -> new IllegalArgumentException("account not found"));

            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("user not found"));

            if (!body.containsKey("password")) {
                return;
            }

            if (user.checkPassword(body.get("password").getFirst())) {
                log.info("user : {}", user);
                Session session = Session.createRandomSession();
                session.setAttribute("user", user);
                Http11Cookie http11Cookie = Http11Cookie.sessionCookie(session.getId());
                sessionManager.add(session);
                HttpResponse httpResponse = new HttpResponse("/index.html", "html", HttpStatusCode.FOUND, http11Cookie, null);
                processRedirect(outputStream, httpResponse);
                return;
            }
            HttpResponse httpResponse = new HttpResponse("/401.html", "html", HttpStatusCode.FOUND, null, null);
            processRedirect(outputStream, httpResponse);
        }

    }

}
