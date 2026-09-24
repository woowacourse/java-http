package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Map<RequestLine, BiFunction<HttpRequest, SessionContext, Response>> requestHandlerMap = Map.of(
        RequestLine.from("GET /index HTTP/1.1"), this::handleIndex,
        RequestLine.from("GET /login HTTP/1.1"), this::handleGetLogin,
        RequestLine.from("POST /login HTTP/1.1"), this::handlePostLogin,
        RequestLine.from("GET /register HTTP/1.1"), this::handleGetRegister,
        RequestLine.from("POST /register HTTP/1.1"), this::handlePostRegister);
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
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
            final var outputStream = connection.getOutputStream()) {
            final HttpRequest request = readRequest(bufferedReader);
            final HttpCookie httpCookie = HttpCookie.from(request.headerValueOf("Cookie"));

            Response response = dispatchRequest(request, httpCookie);
            completeResponse(response);
            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private SessionContext getOrCreateSession(final HttpCookie httpCookie) {
        final SessionManager sessionManager = SessionManager.getInstance();
        final String jSessionId = httpCookie.getValue("JSESSIONID");
        if (sessionManager.findSession(jSessionId) != null) {
            return SessionContext.notCreated(sessionManager.findSession(jSessionId));
        }

        final UUID uuid = UUID.randomUUID();
        final Session newSession = new Session(uuid.toString());
        sessionManager.add(newSession);

        return SessionContext.created(newSession);
    }

    private void completeResponse(final Response response) throws IOException {
        response.addBody(readStaticResource(response.filePath()));
        response.addHeader("Content-Type", getContentType(response.filePath()) + ";charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(response.body()
            .getBytes().length));
    }

    private void writeResponse(final OutputStream outputStream, final Response response)
        throws IOException {
        final String message = generateResponseMessage(response);

        outputStream.write(message.getBytes());
        outputStream.flush();
    }

    private HttpRequest readRequest(final BufferedReader bufferedReader) throws IOException {
        final String rawRequestLine = bufferedReader.readLine().trim();
        final HttpHeaders headers = HttpHeaders.from(readRequestHeaderLines(bufferedReader));
        final String requestBody =
            readRequestBody(bufferedReader, headers.valueOf("Content-Length"));

        return new HttpRequest(
            RequestLine.from(rawRequestLine),
            headers,
            requestBody);
    }

    private List<String> readRequestHeaderLines(final BufferedReader bufferedReader)
        throws IOException {
        final List<String> requestHeaderLines = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            requestHeaderLines.add(line);
        }

        return requestHeaderLines;
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

    private Response dispatchRequest(final HttpRequest request, final HttpCookie httpCookie) {
        final RequestLine requestLine = request.line();
        if (requestLine.isStaticResource()) {
            return Response.ok(requestLine.path());
        }
        if (!requestHandlerMap.containsKey(requestLine)) {
            return Response.notFound();
        }

        final SessionContext sessionContext = getOrCreateSession(httpCookie);
        final Response response = requestHandlerMap.get(requestLine)
            .apply(request, sessionContext);
        if (sessionContext.created()) {
            response.addHeader("Set-Cookie", "JSESSIONID=" + sessionContext.session().id());
        }
        return response;
    }

    private Response handleIndex(final HttpRequest request, final SessionContext sessionContext) {
        return new Response(HttpStatus.OK, "/index.html");
    }

    private Response handleGetLogin(final HttpRequest request, final SessionContext sessionContext) {
        final Session session = sessionContext.session();
        if (session != null && session.hasAttribute("user")) {
            return Response.found("/index.html", "/index");
        }
        return new Response(HttpStatus.OK, "/login.html");
    }

    private Response handlePostLogin(final HttpRequest request, final SessionContext sessionContext) {
        final LoginRequest loginRequest = LoginRequest.from(request.requestBody());
        final Session session = sessionContext.session();
        final Optional<User> filteredUser = InMemoryUserRepository.findByAccount(loginRequest.account())
            .filter(foundUser -> foundUser.checkPassword(loginRequest.password()));

        if (filteredUser.isPresent()) {
            final User user = filteredUser.get();
            log.info("user: {}", user);
            session.addAttribute("user", user);
            return Response.found("/index.html", "/index.html");
        }

        return Response.unauthorized();
    }

    private Response handleGetRegister(final HttpRequest request, final SessionContext sessionContext) {
        return Response.ok("/register.html");
    }

    private Response handlePostRegister(final HttpRequest request, final SessionContext sessionContext) {
        final RegisterRequest registerRequest = parseRegisterRequest(request.requestBody());
        final User newUser =
            new User(registerRequest.account(), registerRequest.password(),
                registerRequest.email());
        InMemoryUserRepository.save(newUser);
        log.info("register: {}", newUser);

        return Response.seeOther("/index.html", "/index");
    }

    private RegisterRequest parseRegisterRequest(final String requestBody) {
        final Map<String, String> registerParams = new LinkedHashMap<>();
        Arrays.stream(requestBody.split("&"))
            .map(paramToken -> paramToken.split("="))
            .forEach(paramPair -> registerParams.put(
                URLDecoder.decode(paramPair[0], StandardCharsets.UTF_8),
                URLDecoder.decode(paramPair[1], StandardCharsets.UTF_8)));

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
