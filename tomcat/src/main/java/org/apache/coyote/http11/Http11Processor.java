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
    private static final List<String> STATIC_RESOURCE_PATHS = List.of(
        "/",
        "/401.html",
        "/assets/chart-area.js",
        "/assets/chart-bar.js",
        "/assets/chart-pie.js",
        "/css/styles.css",
        "/js/scripts.js");

    private final Map<Route, BiFunction<Request, HttpCookie, Response>> routeHandlerMap = Map.of(
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
            final Request request = readRequest(bufferedReader);
            final HttpCookie httpCookie = HttpCookie.from(request.getHeaderValue("Cookie"));

            Response response = dispatchRequest(request, httpCookie);
            response.addBody(readStaticResource(response.filePath()));
            response.addHeader("Content-Type", getContentType(response.filePath()) + charSetOption);
            response.addHeader("Content-Length", String.valueOf(response.body()
                .getBytes().length));
            createSessionIfAbsent(httpCookie, response);

            final String message = generateResponseMessage(response);

            outputStream.write(message.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void createSessionIfAbsent(final HttpCookie httpCookie, final Response response) {
        if (httpCookie.getValue("JSESSIONID") == null) {
            final UUID uuid = UUID.randomUUID();
            SessionManager.getInstance()
                .add(new Session(uuid.toString()));
            response.addHeader("Set-Cookie", "JSESSIONID=" + uuid);
        }
    }

    private Request readRequest(final BufferedReader bufferedReader) throws IOException {
        final String[] requestLineTokens = bufferedReader.readLine()
            .split(" ");
        final Map<String, String> headers = readRequestHeaders(bufferedReader);
        final String requestBody =
            readRequestBody(bufferedReader, headers.get("Content-Length"));
        final ParsedTarget target = parseTarget(requestLineTokens[1]);

        return new Request(
            HttpMethod.valueOf(requestLineTokens[0]),
            PathAliasesResolver.normalize(target.path()),
            extractQueryParams(target.queryString()),
            headers,
            requestBody);
    }

    private ParsedTarget parseTarget(final String uri) {
        final String queryDelimiter = "?";
        final int queryIndex = uri.indexOf(queryDelimiter);
        if (queryIndex == -1) {
            return new ParsedTarget(uri, "");
        }

        return new ParsedTarget(uri.substring(0, queryIndex), uri.substring(queryIndex + 1));
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

    private Response dispatchRequest(final Request request,
        final HttpCookie httpCookie) {
        final Route route = Route.from(request);
        if (routeHandlerMap.containsKey(route)) {
            return routeHandlerMap.get(route)
                .apply(request, httpCookie);
        }
        if (request.httpMethod() == HttpMethod.GET
            && STATIC_RESOURCE_PATHS.contains(request.path())) {
            return Response.ok(request.path());
        }

        return Response.notFound();
    }

    private Response handleIndex(final Request request, final HttpCookie httpCookie) {
        return new Response(HttpStatus.OK, "/index.html");
    }

    private Response handleGetLogin(final Request request,
        final HttpCookie httpCookie) {
        final Session session = SessionManager.getInstance()
            .findSession(httpCookie.getValue("JSESSIONID"));
        if (session.hasAttribute("user")) {
            return Response.found("/index.html", "/index");
        }
        return new Response(HttpStatus.OK, "/login.html");
    }

    private Response handlePostLogin(final Request request,
        final HttpCookie httpCookie) {
        final LoginRequest loginRequest = parseLoginRequest(request.requestBody());
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

    private Response handleGetRegister(final Request request,
        final HttpCookie httpCookie) {
        return Response.ok("/register.html");
    }

    private Response handlePostRegister(final Request request,
        final HttpCookie httpCookie) {
        final RegisterRequest registerRequest = parseRegisterRequest(request.requestBody());
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
