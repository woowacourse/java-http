package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResourceLoader;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
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
            final var reader = new BufferedReader(new InputStreamReader(inputStream));

            final var response = generateResponse(reader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateResponse(final BufferedReader reader) throws IOException {
        final var requestLine = reader.readLine();
        final var parts = requestLine.split(" ");
        final var method = parts[0];
        final var uri = parts[1];
        final var requestHeader = parseRequestHeader(reader);

        if (method.equals("GET") && uri.equals("/")) {
            final var response = new HttpResponse(HttpStatus.OK);
            response.setBody(new ResponseBody("text/html", "Hello world!"));

            return response.getResponse();
        }
        if (method.equals("GET") && ContentMimeType.isEndsWithExtension(uri)) {
            final var responseBody = ResourceLoader.loadStaticResource(uri);
            final var response = new HttpResponse(HttpStatus.OK);
            response.setBody(responseBody);
            return response.getResponse();
        }
        if (method.equals("GET") && uri.equals("/login")) {
            final var cookie = new HttpCookie(requestHeader.getOrDefault("Cookie", "Cookie"));
            if (cookie.hasJSESSIONID()) {
                final var session = SessionManager.getInstance().findSession(cookie.getJSESSIONID());
                final var user = (User) session.getAttribute("user");
                if (InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
                    final var response = new HttpResponse(HttpStatus.FOUND);
                    response.setRedirect("/index.html");
                    return response.getResponse();
                }
            }
        }
        if (method.equals("POST") && uri.equals("/login")) {
            final var responseBody = getRequestBody(reader, Integer.parseInt(requestHeader.get("Content-Length")));
            if (isAuthenticateUser(responseBody)) {
                HttpCookie cookie = new HttpCookie(requestHeader.getOrDefault("Cookie", "Cookie"));
                if (cookie.hasJSESSIONID()) {
                    final var response = new HttpResponse(HttpStatus.FOUND);
                    response.setRedirect("/index.html");
                    return response.getResponse();
                }

                final var session = new Session(UUID.randomUUID().toString());
                InMemoryUserRepository.findByAccount(responseBody.get("account"))
                        .ifPresent(user -> session.setAttribute("user", user));
                final var response = new HttpResponse(HttpStatus.FOUND);
                response.setRedirect("/index.html");
                response.setCookie("JSESSIONID", session.getId());
                return response.getResponse();
            } else {
                final var response = new HttpResponse(HttpStatus.FOUND);
                response.setRedirect("/401.html");
                return response.getResponse();
            }
        }
        if (method.equals("POST") && uri.contains("register")) {
            final var requestBody = getRequestBody(reader, Integer.parseInt(requestHeader.get("Content-Length")));
            final var newUser = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
            InMemoryUserRepository.save(newUser);
            final var response = new HttpResponse(HttpStatus.FOUND);
            response.setRedirect("/index.html");
            return response.getResponse();
        }
        final var response = new HttpResponse(HttpStatus.OK);
        response.setBody(ResourceLoader.loadHtmlResource(uri));
        return response.getResponse();
    }

    private Map<String, String> getRequestBody(final BufferedReader reader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        final var requestBody = new String(buffer);
        return parseQueryParam(requestBody);
    }

    private Map<String, String> parseRequestHeader(final BufferedReader reader) throws IOException {
        final Map<String, String> result = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).isBlank()) {
            String[] parts = line.split(": ", 2);
            result.put(parts[0], parts[1]);
        }
        return result;
    }

    private boolean hasQueryParam(final String uri) {
        return uri.contains("?");
    }

    private Map<String, String> parseQueryParam(final String queryString) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    private boolean isAuthenticateUser(final Map<String, String> queryParams) {
        return InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .filter(user -> user.checkPassword(queryParams.get("password")))
                .map(user -> {
                    log.info("user : {}", user);
                    return true;
                })
                .orElseGet(() -> false);
    }
}
