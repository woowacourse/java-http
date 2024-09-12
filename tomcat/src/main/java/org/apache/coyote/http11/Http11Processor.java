package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.manager.Session;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        sessionManager = SessionManager.getInstance();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final InputStreamReader reader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(reader)) {

            HttpRequest request = new HttpRequest(bufferedReader);
            HttpMethod requestMethod = request.getMethod();

            if (requestMethod == HttpMethod.GET) {
                processGetRequest(request, outputStream);
                return;
            }

            if (requestMethod == HttpMethod.POST) {
                processPostRequest(request, outputStream);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void processGetRequest(HttpRequest request, OutputStream outputStream)
            throws URISyntaxException, IOException {
        String requestUrl = request.getUrl();

        if (requestUrl.equals("/")) {
            getRootRequest(outputStream);
            return;
        }

        if (requestUrl.equals("/login")) {
            HttpCookie cookie = request.getCookie();
            String sessionId = cookie.get("JSESSIONID");

            if (sessionId != null) {
                Session session = sessionManager.findSession(sessionId);
                if (session != null && session.getAttribute("user") != null) {
                    redirect("index.html", outputStream);
                    return;
                }
            }
        }

        processStaticResource(requestUrl, outputStream);
    }

    private void processPostRequest(HttpRequest request, OutputStream outputStream)
            throws URISyntaxException, IOException {
        String requestUrl = request.getUrl();
        Map<String, String> body = getBody(request.getBody());

        if (requestUrl.equals("/register")) {
            postRegisterRequest(outputStream, body);
        }

        if (requestUrl.equals("/login")) {
            postLoginRequest(outputStream, body);
        }
    }

    private void processStaticResource(String requestUrl, OutputStream outputStream)
            throws URISyntaxException, IOException {
        try {
            final Path path = findPath(requestUrl);
            byte[] fileBytes = Files.readAllBytes(path);
            String contentType = URLConnection.guessContentTypeFromName(path.toString());

            HttpResponse response = new HttpResponse(StatusCode.OK);
            response.addHeader("Content-Type", contentType + ";charset=utf-8");
            response.addHeader("Content-Length", String.valueOf(fileBytes.length));
            response.setBody(fileBytes);

            outputStream.write(response.buildResponse());
            outputStream.flush();
        } catch (FileNotFoundException e) {
            redirect("/404.html", outputStream);
        }
    }

    private void postRegisterRequest(OutputStream outputStream, Map<String, String> bodys) throws IOException {
        User user = new User(bodys.get("account"), bodys.get("password"), bodys.get("email"));
        InMemoryUserRepository.save(user);
        redirect("index.html", outputStream);
    }

    private void postLoginRequest(OutputStream outputStream, Map<String, String> bodys) throws IOException {
        try {
            User user = InMemoryUserRepository.findByAccount(bodys.get("account"))
                    .orElseThrow();
            if (!user.checkPassword(bodys.get("password"))) {
                throw new RuntimeException();
            }
            log.debug("user: {}", user);

            Session session = new Session();
            session.setAttribute("user", user);
            sessionManager.add(session);

            HttpResponse response = new HttpResponse(StatusCode.FOUND);
            response.addHeader("Location", "/index.html");
            response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());

            outputStream.write(response.buildResponse());
            outputStream.flush();
        } catch (Exception e) {
            redirect("/401.html", outputStream);
        }
    }

    private void getRootRequest(OutputStream outputStream) throws IOException {
        String body = "Hello world!";

        HttpResponse response = new HttpResponse(StatusCode.OK);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        response.setBody(body);

        outputStream.write(response.buildResponse());
        outputStream.flush();
    }

    private void redirect(String url, OutputStream outputStream) throws IOException {
        HttpResponse response = new HttpResponse(StatusCode.FOUND);
        response.addHeader("Location", url);

        outputStream.write(response.buildResponse());
        outputStream.flush();
    }

    private Path findPath(String requestURL) throws URISyntaxException, FileNotFoundException {
        if (!requestURL.contains(".")) {
            requestURL += ".html";
        }

        URL resource = getClass().getClassLoader().getResource("static" + requestURL);
        if (resource == null) {
            throw new FileNotFoundException();
        }

        return Path.of(resource.toURI());
    }

    private Map<String, String> getBody(String body) {
        Map<String, String> bodys = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyAndValue = pair.split("=");
            bodys.put(keyAndValue[0], keyAndValue[1]);
        }
        return bodys;
    }
}
