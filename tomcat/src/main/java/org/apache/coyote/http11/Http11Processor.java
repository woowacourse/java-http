package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            final var request = HttpRequest.from(reader);
            if (request.getRequestLine() == null) {
                return;
            }

            log.info("request line: {}", request.getRequestLine());

            final var method = request.getRequestMethod();
            final var url = request.getRequestPath();

            if ("/".equals(url)) {
                outputStream.write(HttpResponse.ok("text/html", "Hello world!"));
            }
            if ("/index.html".equals(url)) {
                buildHtmlResponse(outputStream, url);
            }
            if ("/css/styles.css".equals(url)) {
                buildStyleSheetResponse(outputStream, url);
            }
            if ("/js/scripts.js".equals(url)) {
                buildScriptResponse(outputStream, url);
            }
            if (url.matches("/assets/.*\\.js")) {
                buildScriptResponse(outputStream, url);
            }
            if ("/login".equals(url) && "GET".equals(method) && !isLogin(request, outputStream)) {
                buildHtmlResponse(outputStream, url + ".html");
            }
            if ("/login".equals(url) && "POST".equals(method)) {
                login(outputStream, request);
            }
            if ("/register".equals(url) && "GET".equals(method)) {
                buildHtmlResponse(outputStream, url + ".html");
            }
            if ("/register".equals(url) && "POST".equals(method)) {
                register(outputStream, request);
            }

            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isLogin(final HttpRequest request, final OutputStream outputStream) throws IOException {
        final var cookies = request.getCookies();
        return cookies.containsKey("JSESSIONID") && hasSession(outputStream, cookies);
    }

    private boolean hasSession(final OutputStream outputStream, final Map<String, String> cookies) throws IOException {
        final var sessionId = cookies.get("JSESSIONID");
        if (SessionManager.findSession(sessionId) != null) {
            outputStream.write(HttpResponse.found("/index.html"));
            outputStream.flush();
            return true;
        }
        return false;
    }

    private void login(final OutputStream outputStream, final HttpRequest request) throws IOException {
        Map<String, String> params = request.parseRequestQuery();

        if (InMemoryUserRepository.findByAccount(params.get("account")).isEmpty()) {
            buildHtmlResponse(outputStream, "/401.html");
            return;
        }

        final User user = InMemoryUserRepository.findByAccount(params.get("account")).get();
        if (user.checkPassword(params.get("password"))) {
            final UUID id = UUID.randomUUID();
            final Session session = new Session(id.toString());
            session.setAttribute("user", user);
            SessionManager.add(session);

            HttpCookie cookie = new HttpCookie("JSESSIONID=" + id);
            log.info("cookie: {}", cookie.getCookieValue("JSESSIONID"));
            outputStream.write(HttpResponse.found("/index.html", cookie));
            log.info("user: {}", user);
            return;
        }

        buildHtmlResponse(outputStream, "/401.html");
    }

    private void register(final OutputStream outputStream, final HttpRequest request) throws IOException {
        final Map<String, String> userInfos = request.parseRequestQuery();

        final User user = new User(userInfos.get("account"), userInfos.get("password"), userInfos.get("email"));
        InMemoryUserRepository.save(user);

        outputStream.write(HttpResponse.found("/index.html"));
    }

    private String buildResponseBodyFromStaticFile(final String fileName) throws IOException {
        final var resourceName = "static" + fileName;
        final var path = Path.of(this.getClass().getClassLoader().getResource(resourceName).getPath());

        return String.join("\n", Files.readAllLines(path)) + "\n";
    }

    private void buildHtmlResponse(final OutputStream outputStream, final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        outputStream.write(HttpResponse.ok("text/html", responseBody));
    }

    private void buildStyleSheetResponse(final OutputStream outputStream, final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        outputStream.write(HttpResponse.ok("text/css", responseBody));
    }

    private void buildScriptResponse(final OutputStream outputStream, final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        outputStream.write(HttpResponse.ok("text/javascript", responseBody));
    }
}
