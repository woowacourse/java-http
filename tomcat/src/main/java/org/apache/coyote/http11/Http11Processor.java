package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String WELCOME_MESSAGE = "Hello world!";
    private static final String TEXT_HTML = "text/html";
    private static final String TEXT_CSS = "text/css";
    private static final String APPLICATION_JAVASCRIPT = "application/javascript";
    private static final String POST = "POST";
    private static final String GET = "GET";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final String requestStartLine = bufferedReader.readLine();

            final String requestUri = getRequestUri(requestStartLine);
            final String requestMethod = getRequestMethod(requestStartLine);
            final Map<String, String> headers = getHeaders(bufferedReader);

            if (requestMethod.equals(POST)) {
                final int contentLength = Integer.parseInt(headers.get("Content-Length"));
                final Map<String, String> requestBody = getRequestBody(bufferedReader, contentLength);

                if (requestUri.contains("register")) {
                    final User user = new User(requestBody.get("account"), requestBody.get("password"),
                            requestBody.get("email"));
                    InMemoryUserRepository.save(user);

                    saveSessionAndResponse(outputStream, user);
                    return;
                }

                if (requestUri.contains("login")) {
                    final Optional<User> possibleUser = InMemoryUserRepository.findByAccount(
                            requestBody.get("account"));
                    if (possibleUser.isPresent()) {
                        saveSessionAndResponse(outputStream, possibleUser.get());
                        return;
                    }
                }
            }

            if (requestMethod.equals(GET) && requestUri.contains("login") && headers.containsKey("Cookie")
                    && headers.get("Cookie").contains("JSESSION")) {
                final SessionManager sessionManager = new SessionManager();
                final String cookie = headers.get("Cookie");
                final HttpSession session = sessionManager.findSession(cookie.split("JSESSIONID=")[1]);
                final User user = (User) session.getAttribute("user");
                final Optional<User> possibleUser = InMemoryUserRepository.findByAccount(user.getAccount());
                if (possibleUser.isPresent()) {
                    final String response = getFoundResponse();
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }
            }

            final String responseBody = getResponseBody(requestUri);
            final String contentType = getContentType(requestUri);
            final String response = getOKResponse(responseBody, contentType);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void saveSessionAndResponse(final OutputStream outputStream, final User user) throws IOException {
        final UUID sessionId = UUID.randomUUID();
        final String response = getFoundResponse("JSESSIONID=" + sessionId);
        final HttpSession session = new Session(String.valueOf(sessionId));
        session.setAttribute("user", user);
        new SessionManager().add(session);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String getRequestUri(final String requestStartLine) {
        return requestStartLine.split(" ")[1];
    }

    private String getRequestMethod(final String requestStartLine) {
        return requestStartLine.split(" ")[0];
    }

    private String getRequestPath(final String requestUri) {
        if (requestUri.contains("?")) {
            final int index = requestUri.indexOf("?");
            return "static/" + requestUri.substring(0, index);
        }
        return "static/" + requestUri;
    }

    private Map<String, String> getQueryParam(final String requestUri) {
        if (!requestUri.contains("?")) {
            return Collections.emptyMap();
        }
        final int index = requestUri.indexOf("?");
        final String[] queryStrings = requestUri.substring(index + 1)
                .split("&");

        return Arrays.stream(queryStrings)
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1], (a, b) -> b));
    }

    private Map<String, String> getHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        String header;
        while (!"".equals((header = bufferedReader.readLine()))) {
            final String[] splitHeader = header.split(": ");
            headers.put(splitHeader[0], splitHeader[1]);
        }

        return headers;
    }

    private Map<String, String> getRequestBody(final BufferedReader bufferedReader, final int contentLength)
            throws IOException {
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);

        return Arrays.stream(requestBody.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1], (a, b) -> b));
    }


    private String getContentType(final String requestUri) {
        if (requestUri.contains(".css")) {
            return TEXT_CSS;
        }
        if (requestUri.contains(".js")) {
            return APPLICATION_JAVASCRIPT;
        }
        return TEXT_HTML;
    }

    private String getResponseBody(final String requestUri) throws IOException {
        String responseBody = WELCOME_MESSAGE;

        if (!requestUri.equals("/")) {
            String requestPath = getRequestPath(requestUri);
            if (!requestPath.contains(".")) {
                requestPath += ".html";
            }

            final String resource = getClass().getClassLoader()
                    .getResource(requestPath)
                    .getPath();
            final File file = new File(resource);
            final BufferedReader fileReader = new BufferedReader(new FileReader(file));
            responseBody = fileReader.lines()
                    .collect(Collectors.joining("\n"));
            responseBody += "\n";

            fileReader.close();
        }

        return responseBody;
    }

    private String getOKResponse(final String responseBody, final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getFoundResponse(final String cookie) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Set-Cookie: " + cookie + " ",
                "");
    }

    private String getFoundResponse() {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "");
    }
}
