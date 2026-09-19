package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final List<String> ALLOWED_PATHS = List.of(
        "/", "/index.html", "/index", "/login.html", "/login", "/register", "/401.html",
        "/assets/chart-area.js", "/assets/chart-bar.js", "/assets/chart-pie.js",
        "/css/styles.css",
        "/js/scripts.js");

    private final Map<Route, BiFunction<RequestTarget, HttpCookie, Response>> routeHandlerMap = Map.of(
        new Route(HttpMethod.GET, "/index.html"), this::handleIndex,
        new Route(HttpMethod.GET, "/index"), this::handleIndex,
        new Route(HttpMethod.GET, "/login"), this::handleGetLogin,
        new Route(HttpMethod.POST, "/login"), this::handlePostLogin,
        new Route(HttpMethod.GET, "/register"), this::handleGetRegister,
        new Route(HttpMethod.POST, "/register"), this::handlePostRegister);

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
        final String charSetOption = ";charset=utf-8";
        try (final var inputStream = connection.getInputStream();
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
            final var outputStream = connection.getOutputStream()) {
            final RequestTarget requestTarget = getRequestTarget(bufferedReader);
            final HttpCookie httpCookie = HttpCookie.from(requestTarget.getHeaderValue("Cookie"));

            Response response = dispatchRequest(requestTarget, httpCookie);
            response.addBody(readStaticResource(response.filePath()));
            response.addHeader("Content-Type", getContentType(response.filePath()) + charSetOption);
            response.addHeader("Content-Length", String.valueOf(response.body()
                .getBytes().length));
            if (!httpCookie.containsJSessionId()) {
                final UUID uuid = UUID.randomUUID();
                SessionManager.getInstance()
                    .add(new Session(uuid.toString()));
                response.addHeader("Set-Cookie", "JSESSIONID=" + uuid);
            }

            final String message = generateResponseMessage(response);

            outputStream.write(message.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestTarget getRequestTarget(final BufferedReader bufferedReader) throws IOException {
        final String[] requestLineTokens = bufferedReader.readLine()
            .split(" ");
        final Map<String, String> headers = readRequestHeaders(bufferedReader);
        final String requestBody =
            readRequestBody(bufferedReader, headers.get("Content-Length"));
        final Map<String, String> target = parseTarget(requestLineTokens[1]);

        return new RequestTarget(
            HttpMethod.valueOf(requestLineTokens[0]),
            target.get("filePath"),
            extractQueryParams(target.get("queryString")),
            headers,
            requestBody);
    }

    private Map<String, String> parseTarget(String uri) {
        final String queryDelimiter = "?";
        String path = "";
        String queryString = "";
        if (!uri.contains(queryDelimiter)) {
            path = uri;
            queryString = "";
        }
        if (uri.contains(queryDelimiter)) {
            final int queryIndex = uri.indexOf(queryDelimiter);
            path = uri.substring(0, queryIndex);
            queryString = uri.substring(queryIndex + 1);
        }
        return Map.of(
            "filePath", path,
            "queryString", queryString);
    }

    private Map<String, String> readRequestHeaders(final BufferedReader bufferedReader)
        throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while (!Objects.equals(line = bufferedReader.readLine(), "")) {
            final String[] headerLineTokens = line.split(": ");
            headers.put(headerLineTokens[0], headerLineTokens[1]);
        }

        return headers;
    }

    private Map<String, String> extractQueryParams(final String queryString) {
        final Map<String, String> queryParams = new LinkedHashMap<>();
        if (queryString.isBlank()) {
            return queryParams;
        }
        Arrays.stream(queryString.split("&"))
            .map(keyValue -> keyValue.split("="))
            .forEach(split -> queryParams.put(split[0], split[1]));

        return queryParams;
    }

    private String readRequestBody(final BufferedReader bufferedReader,
        final String rawContentLength)
        throws IOException {
        if (rawContentLength == null || rawContentLength.isEmpty()) {
            return "";
        }
        final int contentLength = Integer.parseInt(rawContentLength.trim());
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer).trim();
    }

    private Response dispatchRequest(final RequestTarget requestTarget,
        final HttpCookie httpCookie) {
        final Route route = Route.from(requestTarget);
        if (!ALLOWED_PATHS.contains(requestTarget.path())) {
            return new Response(HttpStatus.NOT_FOUND, "/404.html");
        }
        if (routeHandlerMap.containsKey(route)) {
            return routeHandlerMap.get(route)
                .apply(requestTarget, httpCookie);
        }

        return new Response(HttpStatus.OK, requestTarget.path());
    }

    private Response handleIndex(final RequestTarget requestTarget, final HttpCookie httpCookie) {
        return new Response(HttpStatus.OK, "/index.html");
    }

    private Response handleGetLogin(final RequestTarget requestTarget,
        final HttpCookie httpCookie) {
        final Session session = SessionManager.getInstance()
            .findSession(httpCookie.getValue("JSESSIONID"));
        if (session.hasAttribute("user")) {
            return Response.found("/index.html", "/index");
        }
        return new Response(HttpStatus.OK, "/login.html");
    }

    private Response handlePostLogin(final RequestTarget requestTarget,
        final HttpCookie httpCookie) {
        final LoginRequest loginRequest = parseLoginRequest(requestTarget.requestBody());
        final User user = InMemoryUserRepository.findByAccount(loginRequest.account())
            .orElseThrow();
        if (user.checkPassword(loginRequest.password())) {
            log.info("user: {}", user);
            final Session session = SessionManager.getInstance()
                .findSession(httpCookie.getValue("JSESSIONID"));
            session.addAttribute("user", user);
            return Response.permanentRedirect("/index.html", "/index.html");
        }

        return Response.unauthorized();
    }

    private LoginRequest parseLoginRequest(final String requestBody) {
        final Map<String, String> loginParams = new LinkedHashMap<>();
        Arrays.stream(requestBody.split("&"))
            .map(paramToken -> paramToken.split("="))
            .forEach(paramPair -> loginParams.put(paramPair[0], paramPair[1]));

        return new LoginRequest(
            loginParams.get("account"),
            loginParams.get("password"));
    }

    private Response handleGetRegister(final RequestTarget requestTarget,
        final HttpCookie httpCookie) {
        return Response.ok("/register.html");
    }

    private Response handlePostRegister(final RequestTarget requestTarget,
        final HttpCookie httpCookie) {
        final RegisterRequest registerRequest = parseRegisterRequest(requestTarget.requestBody());
        final User newUser =
            new User(registerRequest.account(), registerRequest.password(),
                registerRequest.email());
        InMemoryUserRepository.save(newUser);
        log.info("register: {}", newUser);

        return Response.permanentRedirect("/index.html", "/index.html");
    }

    private RegisterRequest parseRegisterRequest(final String requestBody) {
        final Map<String, String> registerParams = new LinkedHashMap<>();
        Arrays.stream(requestBody.split("&"))
            .map(paramToken -> paramToken.split("="))
            .forEach(paramPair -> registerParams.put(paramPair[0], paramPair[1]));

        return new RegisterRequest(
            registerParams.get("account"),
            registerParams.get("password"),
            registerParams.get("email"));
    }

    private String getContentType(final String filePath) {
        final String defaultContentType = "text/html";
        if (filePath.equals("/")) {
            return defaultContentType;
        }
        final String prefix = "text/";
        final int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex == 0) {
            throw new IllegalArgumentException("유효한 타겟 uri가 아닙니다.");
        }
        return prefix + filePath.substring(lastDotIndex + 1);
    }

    private String readStaticResource(final String filePath) throws IOException {
        if (filePath.equals("/")) {
            return "Hello world!";
        }
        final String staticResourceTarget = "/static" + filePath;

        final URL resource = getClass().getResource(staticResourceTarget);
        if (resource == null) {
            throw new RuntimeException("요청한 리소스가 존재하지 않습니다 (filePath: " + staticResourceTarget);
        }

        return new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
    }

    private String generateResponseMessage(final Response response) {
        return String.join("\r\n",
            "HTTP/1.1 " + response.httpStatusCode() + " " + response.httpStatusName() + " ",
            response.headerString(),
            "",
            response.body());
    }
}
