package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;

import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return;
            }

            RequestLine requestLine = new RequestLine(readLine);

            Map<String, String> readHttpRequestHeaders = readHeaders(bufferedReader);
            String readRequestBody = readRequestBody(bufferedReader, readHttpRequestHeaders);

            HttpRequest httpRequest = new HttpRequest(requestLine, readHttpRequestHeaders, readRequestBody);

            HttpCookie requestCookie = new HttpCookie(httpRequest.getHeader("cookie"));
            String sessionId = requestCookie.get(HttpCookie.JSESSIONID);
            Session session = SessionManager.getInstance().findSession(sessionId);
            HttpCookie responseCookie = null;

            if (httpRequest.getPath().equals("/login")
                    && httpRequest.getMethod().equals("GET")
                    && httpRequest.getQueryString().isEmpty()) {
                if (session != null && getUser(session) != null) {
                    sendRedirect(outputStream, "/index.html", null);
                    return;
                }
            }

            if (httpRequest.getPath().equals("/login")
                    && (httpRequest.getMethod().equals("POST")
                    || !httpRequest.getQueryString().isEmpty())) {
                if (session == null) {
                    session = new Session(UUID.randomUUID().toString());
                    SessionManager.getInstance().add(session);
                }

                String loginData = httpRequest.getMethod().equals("POST")
                        ? httpRequest.getBody() : httpRequest.getQueryString();
                boolean loginSuccess = login(loginData, session);

                if (loginSuccess) {
                    responseCookie = HttpCookie.ofJSessionId(session.getId());
                }

                String redirectPath = loginSuccess ? "/index.html" : "/401.html";
                sendRedirect(outputStream, redirectPath, responseCookie);
                return;
            }

            if (httpRequest.getPath().equals("/register")
                    && httpRequest.getMethod().equals("POST")) {
                Map<String, String> registerInfo = parseQueryString(httpRequest.getBody());

                String account = registerInfo.get("account");
                String password = registerInfo.get("password");
                String email = registerInfo.get("email");

                User user = new User(account, password, email);
                InMemoryUserRepository.save(user);

                sendRedirect(outputStream, "/index.html", responseCookie);
                return;
            }

            var responseBody = "Hello world!";
            int contentLength = responseBody.getBytes().length;
            String contentType = getContentType(httpRequest.getPath());
            String statusLine = "HTTP/1.1 200 OK ";

            if (!httpRequest.getPath().equals("/")) {
                String resourcePath = getResourcePath(httpRequest.getPath());

                byte[] fileBytes = readResource(resourcePath);

                if (fileBytes == null) {
                    fileBytes = readResource("static/404.html");
                    if (fileBytes == null) {
                        throw new IllegalArgumentException("404.html 리소스를 찾을 수 없습니다.");
                    }
                    statusLine = "HTTP/1.1 404 Not Found ";
                }

                responseBody = new String(fileBytes, StandardCharsets.UTF_8);
                contentLength = fileBytes.length;
            }

            String cookieHeader = "";

            if (responseCookie != null) {
                cookieHeader = "Set-Cookie: " + responseCookie.toHeaderValue();
            }

            List<String> headers = new ArrayList<>();
            headers.add(statusLine);
            headers.add("Content-Type: " + contentType + " ");
            if (!cookieHeader.isBlank()) {
                headers.add(cookieHeader);
            }
            headers.add("Content-Length: " + contentLength + " ");
            headers.add("");
            headers.add(responseBody);

            final var response = String.join("\r\n", headers);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | URISyntaxException e) {
            log.error("HTTP 요청 처리 중 입출력 오류가 발생했습니다.", e);
        } catch (RuntimeException e) {
            log.error("HTTP 요청 처리 중 예상하지 못한 오류가 발생했습니다.", e);
        }
    }

    private byte[] readResource(String resourcePath) throws IOException, URISyntaxException {
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            return null;
        }
        URI resourceUri = resourceUrl.toURI();
        Path path = Paths.get(resourceUri);

        return Files.readAllBytes(path);
    }

    private Map<String, String> readHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine;

        while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {
            int separatorIndex = headerLine.indexOf(":");

            if (separatorIndex == -1) {
                continue;
            }

            String headerName = headerLine
                    .substring(0, separatorIndex)
                    .trim()
                    .toLowerCase(Locale.ROOT);

            String headerValue = headerLine
                    .substring(separatorIndex + 1)
                    .trim();

            headers.put(headerName, headerValue);
        }

        return headers;
    }

    private String readRequestBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        String contentLengthValue = headers.get("content-length");

        if (contentLengthValue == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthValue);
        char[] buffer = new char[contentLength];

        int totalRead = 0;

        while (totalRead < contentLength) {
            int read = bufferedReader.read(buffer, totalRead, contentLength - totalRead);

            if (read == -1) {
                break;
            }

            totalRead += read;
        }

        return new String(buffer, 0, totalRead);
    }

    private String getResourcePath(String path) {
        if (path.equals("/login")) {
            return "static/login.html";
        }

        if (path.equals("/register")) {
            return "static/register.html";
        }

        return "static" + path;
    }

    private boolean login(String queryString, Session session) {
        Map<String, String> loginInfo = parseQueryString(queryString);

        String account = loginInfo.get("account");
        String password = loginInfo.get("password");

        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        var optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        if (!user.checkPassword(password)) {
            return false;
        }

        session.setAttribute("user", user);
        log.info("login user: {}", user);

        return true;
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }

    private void sendRedirect(OutputStream outputStream, String redirectPath, HttpCookie responseCookie)
            throws IOException {
        String cookieHeader = "";

        if (responseCookie != null) {
            cookieHeader = "Set-Cookie: " + responseCookie.toHeaderValue();
        }

        String response = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + redirectPath,
                cookieHeader,
                "Content-Length: 0",
                "",
                "");

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parameters = new HashMap<>();

        for (String parameter : queryString.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length != 2) {
                continue;
            }

            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            parameters.put(key, value);
        }

        return parameters;
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html;charset=utf-8";
    }
}
