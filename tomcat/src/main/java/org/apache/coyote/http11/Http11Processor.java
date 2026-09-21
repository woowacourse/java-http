package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String CRLF = "\r\n";
    private static final String STATIC_RESOURCE_ROOT = "static";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String EMAIL_PARAMETER = "email";

    private static final String ACCEPT_HEADER = "Accept";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String ACCEPT_ANY = "*/*";
    private static final String TEXT_HTML = "text/html;charset=utf-8";
    private static final String TEXT_CSS = "text/css";

    private static final String STATUS_OK = "HTTP/1.1 200 OK ";
    private static final String STATUS_FOUND = "HTTP/1.1 302 Found ";
    private static final String STATUS_NOT_FOUND = "HTTP/1.1 404 Not Found ";

    private static final String POST = "POST";

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
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final var requestLine = readRequestLine(bufferedReader);
            if (requestLine == null) {
                return;
            }
            final var requestHeaders = readHeaders(bufferedReader);
            final var cookie = new HttpCookie(requestHeaders.get(COOKIE_HEADER));
            var setCookie = "";
            if (!cookie.hasJSessionId()) {
                setCookie = HttpCookie.JSESSIONID + "=" + UUID.randomUUID();
            }
            final var contentType = decideContentType(requestHeaders);
            final var uri = parseUri(requestLine);
            final var method = parseMethod(requestLine);
            final var pathAndQueryString = splitPathAndQueryString(uri);
            final var path = pathAndQueryString[0];
            var queryString = "";
            if (pathAndQueryString.length == 2) {
                queryString = pathAndQueryString[1];
            }
            final var queryParameters = parseQueryParameters(queryString);

            var statusLine = STATUS_OK;
            var location = "";
            final byte[] responseBody;
            if (path.equals("/")) {
                responseBody = "Hello world!".getBytes();
            } else if (method.equals(POST) && path.equals(REGISTER_PATH)) {
                final var requestBody = readBody(bufferedReader, requestHeaders);
                final var formParameters = parseQueryParameters(requestBody);
                register(formParameters);

                statusLine = STATUS_FOUND;
                location = INDEX_PAGE;
                responseBody = new byte[0];
            } else if (method.equals(POST) && path.equals(LOGIN_PATH)) {
                final var requestBody = readBody(bufferedReader, requestHeaders);
                final var formParameters = parseQueryParameters(requestBody);

                statusLine = STATUS_FOUND;
                if (login(formParameters)) {
                    location = INDEX_PAGE;
                } else {
                    location = UNAUTHORIZED_PAGE;
                }
                responseBody = new byte[0];
            } else {
                var resourceUrl = findResource(path);
                if (resourceUrl == null) {
                    statusLine = STATUS_NOT_FOUND;
                    resourceUrl = findResource(NOT_FOUND_PAGE);
                }
                log.info("{} -> {}", statusLine, resourceUrl);
                responseBody = Files.readAllBytes(Path.of(resourceUrl.toURI()));
            }

            final var response = buildResponse(statusLine, location, setCookie, contentType, responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String readRequestLine(final BufferedReader reader) throws IOException {
        final var requestLine = reader.readLine();
        log.info("requestLine: {}", requestLine);
        return requestLine;
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            final String[] nameAndValue = headerLine.split(":", 2);
            headers.put(nameAndValue[0], nameAndValue[1].trim());
        }
        log.info("headers: {}", headers);
        return headers;
    }

    private String readBody(final BufferedReader reader, final Map<String, String> requestHeaders)
            throws IOException {
        final int contentLength = Integer.parseInt(requestHeaders.get(CONTENT_LENGTH_HEADER));
        final char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private String decideContentType(final Map<String, String> requestHeaders) {
        final var accept = requestHeaders.getOrDefault(ACCEPT_HEADER, ACCEPT_ANY);
        if (accept.contains(TEXT_CSS)) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }

    private String parseUri(final String requestLine) {
        final String[] tokens = requestLine.split(" ");
        return tokens[1];
    }

    private String parseMethod(final String requestLine) {
        final String[] tokens = requestLine.split(" ");
        return tokens[0];
    }

    private String[] splitPathAndQueryString(final String uri) {
        return uri.split("\\?", 2);
    }

    private Map<String, String> parseQueryParameters(final String queryString) {
        final Map<String, String> parameters = new LinkedHashMap<>();
        if (queryString.isEmpty()) {
            return parameters;
        }
        final String[] pairs = queryString.split("&");
        for (int i = 0; i < pairs.length; i++) {
            final String[] nameAndValue = pairs[i].split("=", 2);
            parameters.put(URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8));
        }
        return parameters;
    }

    private void register(final Map<String, String> formParameters) {
        final var user = new User(formParameters.get(ACCOUNT_PARAMETER),
                formParameters.get(PASSWORD_PARAMETER),
                formParameters.get(EMAIL_PARAMETER));
        InMemoryUserRepository.save(user);
        log.info("register success: {}", user);
    }

    private boolean login(final Map<String, String> formParameters) {
        final var account = formParameters.get(ACCOUNT_PARAMETER);
        final var password = formParameters.get(PASSWORD_PARAMETER);
        final var user = InMemoryUserRepository.findByAccount(account)
                .filter(found -> found.checkPassword(password));
        user.ifPresent(found -> log.info("login success: {}", found));
        return user.isPresent();
    }

    private URL findResource(final String path) {
        if (path.contains(".")) {
            return getClass().getClassLoader().getResource(STATIC_RESOURCE_ROOT + path);
        }
        return getClass().getClassLoader().getResource(STATIC_RESOURCE_ROOT + path + ".html");
    }

    private String buildResponse(final String statusLine, final String location, final String setCookie,
                                 final String contentType, final byte[] body) {
        final List<String> lines = new ArrayList<>();
        lines.add(statusLine);
        if (!location.isEmpty()) {
            lines.add("Location: " + location + " ");
        }
        if (!setCookie.isEmpty()) {
            lines.add("Set-Cookie: " + setCookie + " ");
        }
        lines.add("Content-Type: " + contentType + " ");
        lines.add("Content-Length: " + body.length + " ");
        lines.add("");
        lines.add(new String(body, StandardCharsets.UTF_8));
        return String.join(CRLF, lines);
    }
}
