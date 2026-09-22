package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final String request = readRequest(reader);
            log.info(request);

            String method = request.split(" ")[0];
            String uri = request.split(" ")[1];
            String body = readRequestBody(method, request, reader);
            log.info("body: {}", body);
            final String type = findType(uri);
            String status = "200 OK";
            HttpCookie httpCookie = new HttpCookie(findCookies(request));
            boolean hasJSessionId = hasJSessionId(httpCookie);
            String jSessionId = httpCookie.get("JSESSIONID");
            if (!hasJSessionId) {
                jSessionId = UUID.randomUUID().toString();
            }

            final SessionManager manager = SessionManager.getInstance();

            if (uri.equals("/login") && method.equals("GET")) {
                if (hasJSessionId) {
                    log.info("쿠키 존재!");
                    String id = httpCookie.get("JSESSIONID");
                    log.info("session: {}", manager.findSession(id));
                    if (manager.findSession(id) != null) {
                        log.info("세션 존재!");
                        uri = "/index.html";
                    }
                }
            }

            if (!body.isEmpty()) {
                Map<String, String> pairs = findQueries(body);
                if (uri.equals("/login")) {
                    if (userMatching(pairs.get("account"), pairs.get("password"))) {
                        log.info("로그인 성공! id: {}", pairs.get("account"));
                        if (manager.findSession(jSessionId) == null) {
                            String account = pairs.get("account");
                            Session session = new Session(jSessionId);
                            boolean isPresent = InMemoryUserRepository.findByAccount(account).isPresent();
                            if (isPresent) {
                                User user = InMemoryUserRepository.findByAccount(account).get();
                                session.setAttribute("user", user);
                                manager.add(session);
                            }
                        }
                        status = "302 FOUND";
                        uri = "/index.html";
                    } else {
                        uri = "/401.html";
                    }
                } else {
                    User user = new User(pairs.get("account"), pairs.get("password"), pairs.get("email"));
                    log.info("user: {}", user);
                    InMemoryUserRepository.save(user);
                    uri = "/index.html";
                }
            }

            log.info("uri: {}", uri);

            final String responseBody = makeResponseBody(uri);
            final String response = makeResponse(status, type, responseBody, hasJSessionId, jSessionId);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequest(BufferedReader reader) throws IOException {
        final StringBuilder builder = new StringBuilder();

        String line;
        while (!(line = reader.readLine()).isBlank()) {
            builder.append(line);
            builder.append("\r\n");
        }
        return builder.toString();
    }

    private String readRequestBody(String method, String request, BufferedReader reader) throws IOException {
        if (!method.equals("POST")) {
            return "";
        }
        String bodyLength = request.split("Content-Length:", 2)[1].trim();
        bodyLength = bodyLength.split("\r\n")[0].trim();
        int length = Integer.parseInt(bodyLength);
        char[] body = new char[length];
        reader.read(body, 0, length);
        return new String(body);
    }

    private String findType(String uri) {
        if (uri.contains(".")) {
            return List.of(uri.split("\\.")).getLast();
        }
        return "html";
    }

    private boolean hasJSessionId(HttpCookie httpCookie) {
        return httpCookie.containsKey("JSESSIONID");
    }

    private String findCookies(String request) {
        if (!request.contains("Cookie")) {
            return "";
        }
        String allCookies = request.split("Cookie: ", 2)[1];
        log.info("allCookies: {}", allCookies);
        return allCookies.trim();
    }

    private Map<String, String> findQueries(String queryString) {
        final List<String> queries = List.of(queryString.split("&"));
        final Map<String, String> pairs = new HashMap<>();

        for (String query : queries) {
            String[] pair = query.split("=", 2);
            pairs.put(pair[0], pair[1]);
        }
        return pairs;
    }

    private boolean userMatching(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }

    private String makeResponseBody(String uri) throws IOException {
        if (uri.equals("/") || uri.isBlank()) {
            uri = "/index.html";
        }
        if (uri.equals("/login") || uri.equals("/register")) {
            uri = uri + ".html";
        }
        final URL resource = getClass().getClassLoader().getResource("static" + uri);

        final String filePath = resource.getFile();
        final Path path = Paths.get(filePath);

        return Files.readString(path);
    }

    private String setCookie(boolean hasJSessionId, String jSessionId) {
        if (hasJSessionId) {
            return "";
        }
        return "Set-Cookie: JSESSIONID=" + jSessionId + " ";
    }

    private String makeResponse(String status, String type, String responseBody, boolean hasJSessionId,
                                String jSessionId) {

        final String setCookie = setCookie(hasJSessionId, jSessionId);
        return String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                setCookie,
                "",
                responseBody);
    }
}
