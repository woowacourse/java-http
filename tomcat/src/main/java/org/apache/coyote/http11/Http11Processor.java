package org.apache.coyote.http11;

import static org.apache.coyote.http11.utils.UriUtils.extractExtension;
import static org.apache.coyote.http11.utils.UriUtils.getParameters;
import static org.apache.coyote.http11.utils.UriUtils.parsePath;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest request = parseRequest(bufferedReader);

            final String path = parsePath(request.getPath());
            final URL resource = getResourceUrl(path);

            if (resource == null) {
                sendResponse(generateErrorResponse(HttpStatus.NOT_FOUND), outputStream);
                return;
            }

            final Map<String, String> queryParams = getParameters(
                    request.getQueryString(),
                    request.getBodyAsString()
            );

            final HttpCookie httpCookie = parseCookieFromHeader(request.getHeaders());

            if (HttpMethod.POST == request.getMethod() && !queryParams.isEmpty()) {
                if ("/login.html".equals(path)) {
                    handleLogin(queryParams, httpCookie, outputStream);
                    return;
                }

                if ("/register.html".equals(path)) {
                    handleRegister(queryParams, httpCookie, outputStream);
                    return;
                }
            }

            if (HttpMethod.GET == request.getMethod() && "/login.html".equals(path) && httpCookie.contains("JSESSIONID")) {
                final String sessionId = httpCookie.getValue("JSESSIONID");
                if (SessionManager.getInstance().findSession(sessionId).isPresent()) {
                    sendResponse(generateRedirectResponse("/index.html"), outputStream);
                    return;
                }
            }

            sendResponse(generateResponse(HttpStatus.OK, resource), outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(final BufferedReader bufferedReader) throws IOException {
        final String[] request = bufferedReader.readLine().split(" ");
        final RequestLine requestLine = new RequestLine(HttpMethod.valueOf(request[0]), request[1], request[2]);
        final List<String> lines = getHeaders(bufferedReader);
        final Map<String, List<String>> headers = parseHeaders(lines);
        final HttpHeaders httpHeaders = new HttpHeaders(headers);
        final int contentLength = getContentLengthFromHeaders(lines);
        final byte[] body = readRequestBody(bufferedReader, contentLength);

        return new HttpRequest(requestLine, httpHeaders, body);
    }

    private List<String> getHeaders(final BufferedReader reader) throws IOException {
        return reader.lines()
                .takeWhile(line -> !line.isBlank())
                .collect(Collectors.toList());
    }

    private Map<String, List<String>> parseHeaders(List<String> lines) {
        return lines.stream()
                .map(line -> line.split(":", 2))
                .collect(Collectors.groupingBy(
                        arr -> arr[0].trim(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                arr -> arr.length > 1 ? arr[1].trim() : "",
                                Collectors.toList()
                        )
                ));
    }

    private int getContentLengthFromHeaders(final List<String> headers) {
        return headers.stream()
                .filter(h -> h.startsWith("Content-Length"))
                .map(h -> h.split(":")[1].trim())
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(0);
    }

    private byte[] readRequestBody(final BufferedReader reader, final int contentLength) throws IOException {
        if (contentLength <= 0) {
            return new byte[0];
        }

        char[] bodyChars = new char[contentLength];
        int read = reader.read(bodyChars, 0, contentLength);

        if (read == -1) {
            return new byte[0];
        }

        return new String(bodyChars, 0, read).getBytes(StandardCharsets.UTF_8);
    }

    private URL getResourceUrl(String path) throws FileNotFoundException {
        return getClass()
                .getClassLoader()
                .getResource("static" + path);
    }

    private void sendResponse(final HttpResponse response, final OutputStream outputStream) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }

    private HttpCookie parseCookieFromHeader(final HttpHeaders headers) {
        Optional<String> cookieHeader = headers.get("Cookie");
        return cookieHeader.map(HttpCookie::fromHeader)
                .orElseGet(() -> HttpCookie.fromHeader(null));
    }

    private HttpResponse generateResponse(final HttpStatus httpStatus, final URL resource) throws IOException {
        final String resourceName = resource.getFile();
        final String extension = extractExtension(resourceName);
        final byte[] responseBody = Files.readAllBytes(new File(resourceName).toPath());
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setHeader("Content-Type", MimeType.getOrDefault(extension).getType());

        return HttpResponse.of(httpStatus, httpHeaders, responseBody);
    }

    private HttpResponse generateRedirectResponse(final String location) {
        return HttpResponse.redirect(location);
    }

    private HttpResponse generateRedirectResponse(final String location, final HttpCookie httpCookie) {
        final HttpResponse httpResponse = HttpResponse.redirect(location);
        httpCookie.getAll().forEach((name, value) -> httpResponse.setHeader("Set-Cookie", name + "=" + value));
        return httpResponse;
    }

    private HttpResponse generateErrorResponse(final HttpStatus httpStatus) {
        try {
            final String extension = "html";
            final URL resource = getResourceUrl("/" + httpStatus.getCode() + "." + extension);
            final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());
            final MimeType contentType = MimeType.getOrDefault(extension);
            final HttpResponse httpResponse = HttpResponse.error(httpStatus, responseBody);
            httpResponse.setHeader("Content-Type", contentType.getType());
            return httpResponse;
        } catch (IOException | NullPointerException e) {
            final String responseBody = String.format("""
                        <html>
                            <head><title>Error</title></head>
                            <body><h1>%s</h1></body>
                        </html>
                    """, httpStatus.getCode() + " " + httpStatus.getReasonPhrase());
            final HttpResponse httpResponse = HttpResponse.error(httpStatus, responseBody.getBytes(StandardCharsets.UTF_8));
            httpResponse.setHeader("Content-Type", MimeType.HTML.getType());
            return httpResponse;
        }
    }

    private void handleLogin(
            final Map<String, String> queryMap,
            final HttpCookie httpCookie,
            final OutputStream outputStream
    ) throws IOException {
        final String account = queryMap.get("account");
        final String password = queryMap.get("password");

        if (account == null || password == null || account.isBlank() || password.isBlank()) {
            sendResponse(generateErrorResponse(HttpStatus.BAD_REQUEST), outputStream);
            return;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
            final Session session = Session.create();
            session.setAttribute("user", user.get());
            SessionManager.getInstance().add(session);
            httpCookie.add("JSESSIONID", session.getId());
            sendResponse(generateRedirectResponse("/index.html", httpCookie), outputStream);
            return;
        }

        sendResponse(generateRedirectResponse("/401.html"), outputStream);
    }

    private void handleRegister(
            final Map<String, String> queryMap,
            final HttpCookie httpCookie,
            final OutputStream outputStream
    ) throws IOException {
        final String account = queryMap.get("account");
        final String email = queryMap.get("email");
        final String password = queryMap.get("password");

        if (account == null || email == null || password == null
                || account.isBlank() || password.isBlank() || email.isBlank()) {
            sendResponse(generateErrorResponse(HttpStatus.BAD_REQUEST), outputStream);
            return;
        }

        final Optional<User> existingUser = InMemoryUserRepository.findByAccount(account);
        if (existingUser.isPresent()) {
            sendResponse(generateRedirectResponse("/400.html"), outputStream);
            return;
        }

        final User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        log.info("new user : {}", newUser);
        final Session session = Session.create();
        session.setAttribute("user", newUser);
        SessionManager.getInstance().add(session);
        httpCookie.add("JSESSIONID", session.getId());
        sendResponse(generateRedirectResponse("/index.html", httpCookie), outputStream);
    }
}
