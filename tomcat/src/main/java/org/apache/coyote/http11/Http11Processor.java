package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.generator.RandomSessionIdGenerator;
import nextstep.jwp.generator.SessionIdGenerator;
import nextstep.jwp.model.User;
import org.apache.cookie.Cookie;
import org.apache.coyote.Processor;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
            String httpLine = readHttpLine(reader);
            String httpMethod = readHttpMethod(httpLine);
            String resourceName = getResourceName(httpLine);
            Map<String, String> httpRequestHeaders = readRequestHeaders(reader);

            HttpRequests httpRequests = HttpRequests.ofResourceNameAndMethod(resourceName, httpMethod);

            String response = toHttpResponse(httpRequests, httpRequestHeaders, reader).serialize();
            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException | URISyntaxException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readHttpLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private String readHttpMethod(String httpLine) {
        String resourceName = httpLine.split(" ")[0];
        return resourceName;
    }

    private String getResourceName(String httpLine) {
        String resourceName = httpLine.split(" ")[1];
        return resourceName;
    }

    private Map<String, String> readRequestHeaders(BufferedReader reader) throws IOException {
        List<String> headers = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if ("".equals(line)) {
                break;
            }
            headers.add(line);
        }
        return headers.stream()
                .map(header -> header.split(": "))
                .collect(Collectors.toMap(value -> value[0], value -> value[1])
                );
    }

    public HttpResponse toHttpResponse(HttpRequests httpRequests, Map<String, String> httpRequestHeaders, BufferedReader reader) throws IOException, URISyntaxException {
        if (httpRequests.equals(HttpRequests.NOT_FOUND)) {
            String responseBody = "404 Not Found";
            return HttpResponse.of(httpRequests, responseBody);
        }
        if (httpRequests.equals(HttpRequests.LOGIN)) {
            Cookie cookie = Cookie.from(httpRequestHeaders.get("Cookie"));
            if (cookie.hasJSessionId()) {
                String jsessionid = cookie.getCookies().get("JSESSIONID");
                if (SessionManager.findSession(jsessionid) != null) {
                    Session session = SessionManager.findSession(jsessionid);
                    if (session.getAttribute("user") != null) {
                        return HttpResponse.ofLoginRedirect(HttpRequests.FOUND, jsessionid);
                    }

                }
            }
        }
        if (httpRequests.equals(HttpRequests.LOGIN_POST)) {
            if (httpRequestHeaders.containsKey("Content-Length")) {
                String body = readHttpBody(httpRequestHeaders, reader);
                Map<String, String> userInfo = extractUserInfo(body);
                Optional<User> user = InMemoryUserRepository.findByAccount(userInfo.get("account"));
                if (user.isEmpty() || !user.get().checkPassword(userInfo.get("password"))) {
                    Path path = HttpRequests.UNAUTHORIZED.readPath();
                    return HttpResponse.of(httpRequests, new String(readBytes(path)));
                }
                log.info("로그인 성공 : user = {}", user.get());
                SessionIdGenerator sessionIdGenerator = new RandomSessionIdGenerator();
                String jSessionId = sessionIdGenerator.generate();
                Map<String, Object> value = new HashMap<>();
                value.put("user", user.get());
                Session session = new Session(jSessionId, value);
                SessionManager.add(session);

                return HttpResponse.ofLoginRedirect(httpRequests, jSessionId);
            }
        }
        if (httpRequests.equals(HttpRequests.REGISTER_MEMBER)) {
            if (httpRequestHeaders.containsKey("Content-Length")) {
                String body = readHttpBody(httpRequestHeaders, reader);
                Map<String, String> userInfo = extractUserInfo(body);
                User user = new User(userInfo.get("account"), userInfo.get("password"), userInfo.get("email"));
                InMemoryUserRepository.save(user);
                log.info("등록 성공 : user = {}", user);
                return HttpResponse.of(httpRequests, new String(readBytes(HttpRequests.INDEX.readPath())));
            }
        }
        return HttpResponse.of(httpRequests, new String(readBytes(httpRequests.readPath())));
    }

    private Map<String, String> extractUserInfo(String body) {
        return Arrays.stream(body.split("&"))
                .map(value -> value.split("="))
                .collect(Collectors.toMap(value -> value[0], value -> value[1]));
    }

    private String readHttpBody(Map<String, String> httpRequestHeaders, BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }
}
