package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final String STATIC_PATH = "static";
    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String DEFAULT_CONTENT_TYPE = "html";
    private static final String URI_HEADER_KEY = "Uri";
    private static final String ACCEPT_HEADER_KEY = "Accept";
    private static final String ALL_MIME_TYPE = "*/*";
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
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final Map<String, String> httpRequestHeader = readHttpRequestHeader(bufferedReader);
            final Map<String, String> httpResponseHeader = new HashMap<>();

            final String requestUrl = getRequestUrl(httpRequestHeader);

            httpResponseHeader.put("Status", "200 OK");
            if (!requestUrl.equals("/") && !requestUrl.contains(".")) {
                String newRequestUrl = requestUrl + "." + DEFAULT_CONTENT_TYPE;
                String requestUri = httpRequestHeader.get(URI_HEADER_KEY);
                requestUri = requestUri.replace(requestUrl, newRequestUrl);
                httpRequestHeader.put(URI_HEADER_KEY, requestUri);
            }

            String responseBody = "";
            if (isHttpMethod(httpRequestHeader, "GET")) {
                if (requestUrl.equals("/login") && isAlreadyLogin(httpRequestHeader)) {
                    httpResponseHeader.put("Status", "302 Found");
                    httpResponseHeader.put("Location", "/index.html");
                } else {
                    responseBody = getResponseBody(httpRequestHeader);
                    httpResponseHeader.put("Content-Type", getMimeType(httpRequestHeader) + ";charset=utf-8");
                    httpResponseHeader.put("Content-Length", String.valueOf(responseBody.getBytes().length));
                }
            }

            if (isHttpMethod(httpRequestHeader, "POST")) {
                final String httpRequestBody = readHttpRequestBody(bufferedReader, httpRequestHeader);

                httpResponseHeader.put("Status", "302 Found");
                httpResponseHeader.put("Location", "/index.html");
                if (requestUrl.equals("/login")) {
                    loginUser(httpRequestBody, httpRequestHeader, httpResponseHeader);
                } else if (requestUrl.equals("/register")) {
                    saveUser(httpRequestBody);
                }
            }

            final var response = makeHttpResponseHeader(httpResponseHeader, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHttpRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> httpRequestHeader = new HashMap<>();

        final String requestUri = bufferedReader.readLine();
        httpRequestHeader.put(URI_HEADER_KEY, requestUri);

        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            final List<String> splitLine = Arrays.asList(line.split(": "));
            httpRequestHeader.put(splitLine.get(0).trim(), splitLine.get(1).trim());
        }

        return httpRequestHeader;
    }

    private String readHttpRequestBody(BufferedReader bufferedReader, Map<String, String> httpRequestHeader) throws IOException {
        if (!httpRequestHeader.containsKey("Content-Length")) {
            return "";
        }
        int contentLength = Integer.parseInt(httpRequestHeader.get("Content-Length"));
        char[] buffer = new char[contentLength];
        int charsRead = bufferedReader.read(buffer, 0, contentLength);
        if (charsRead == -1) {
            return "";
        }
        return new String(buffer);
    }

    private void loginUser(String httpRequestBody, Map<String, String> httpRequestHeader, Map<String, String> httpResponseHeader) {
        User user = getUser(httpRequestBody);
        if (user == null) {
            httpResponseHeader.put("Location", "/401.html");
            return;
        }
        String password = getUserInformation(httpRequestBody).get("password");
        if (user.checkPassword(password)) {
            processCookie(user, httpRequestHeader, httpResponseHeader);
        } else if (!user.checkPassword(password)) {
            httpResponseHeader.put("Location", "/401.html");
        }
    }

    private void saveUser(String queryString) {
        Map<String, String> userInformation = getUserInformation(queryString);

        if (!userInformation.containsKey("account") || !userInformation.containsKey("password") || !userInformation.containsKey("email")) {
            return;
        }

        String account = userInformation.get("account");
        String password = userInformation.get("password");
        String email = userInformation.get("email");

        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }

    private void processCookie(User user, Map<String, String> httpRequestHeader, Map<String, String> httpResponseHeader) {
        final SessionManager sessionManager = SessionManager.getInstance();
        final String sessionId = getSession(httpRequestHeader);

        if (!sessionId.isEmpty()) {
            return;
        }
        String newSessionId = UUID.randomUUID().toString();
        Session session = new Session(newSessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);
        httpResponseHeader.put("Set-Cookie", "JSESSIONID=" + newSessionId);
    }

    private String makeHttpResponseHeader(Map<String, String> httpResponseHeader, String responseBody) {
        StringBuilder response = new StringBuilder();
        String statusLine = "HTTP/1.1 " + httpResponseHeader.get("Status") + "\r\n";
        response.append(statusLine);

        httpResponseHeader.entrySet().stream()
                .filter(entry -> !"Status".equals(entry.getKey()))
                .map(entry -> entry.getKey() + ": " + entry.getValue() + "\r\n")
                .forEach(response::append);
        response.append("\r\n");
        response.append(responseBody);

        return response.toString();
    }

    private boolean isAlreadyLogin(Map<String, String> httpRequestHeader) {
        SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = getSession(httpRequestHeader);
        if (sessionId.isEmpty()) {
            return false;
        }
        Session session = sessionManager.findSession(sessionId);

        if (session != null) {
            User user = (User) session.getAttribute("user");
            return user != null;
        }
        return false;
    }

    private String getSession(Map<String, String> httpRequestHeader) {
        if (httpRequestHeader.containsKey("Cookie")) {
            HttpCookie httpCookie = new HttpCookie(httpRequestHeader.get("Cookie"));
            return httpCookie.getJSessionId();
        }
        return "";
    }

    private boolean isHttpMethod(Map<String, String> httpRequestHeader, final String httpMethod) {
        return getHttpMethod(httpRequestHeader).equals(httpMethod);
    }

    private String getHttpMethod(Map<String, String> httpRequestHeader) {
        final String uriLine = httpRequestHeader.get(URI_HEADER_KEY);
        return Arrays.asList(uriLine.split(" ")).getFirst();
    }

    private User getUser(String httpRequestBody) {
        Map<String, String> userInformation = getUserInformation(httpRequestBody);

        if (!userInformation.containsKey("account")) {
            return null;
        }
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(userInformation.get("account"));
        return optionalUser.orElse(null);
    }

    private Map<String, String> getUserInformation(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(line -> line.split("="))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
    }

    private String getResponseBody(final Map<String, String> httpRequestHeader) throws IOException {
        String requestUrl = getRequestUrl(httpRequestHeader);
        if (requestUrl.equals("/")) {
            return DEFAULT_MESSAGE;
        }

        final URL resourceUrl = getClass().getClassLoader().getResource(STATIC_PATH + requestUrl);

        if (resourceUrl == null) {
            return "";
        }

        final File file = new File(resourceUrl.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String getMimeType(final Map<String, String> httpRequestHeader) throws IOException {
        final String requestUrl = getRequestUrl(httpRequestHeader);
        final String acceptLine = httpRequestHeader.getOrDefault(ACCEPT_HEADER_KEY, "");
        final String mimeType = Arrays.asList(acceptLine.split(",")).getFirst();

        if (mimeType.isEmpty() || mimeType.equals(ALL_MIME_TYPE)) {
            final String extension = getExtension(requestUrl);
            return MimeTypeMaker.getMimeTypeFromExtension(extension);
        }

        return mimeType;
    }

    private String getRequestUrl(final Map<String, String> httpRequestHeader) {
        final String uriLine = httpRequestHeader.get(URI_HEADER_KEY);
        return Arrays.asList(uriLine.split(" ")).get(1);
    }

    private String getExtension(final String requestUrl) {
        if (requestUrl.equals("/")) {
            return DEFAULT_CONTENT_TYPE;
        }
        return Arrays.asList(requestUrl.split("\\.")).getLast();
    }
}
