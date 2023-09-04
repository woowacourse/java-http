package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.HttpCookie;
import org.apache.coyote.Processor;
import org.apache.coyote.Session;
import org.apache.coyote.request.HttpRequestBody;
import org.apache.coyote.request.HttpRequestHeader;
import org.apache.coyote.request.HttpRequestLine;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseHeader;
import org.apache.coyote.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            HttpRequestLine httpRequestLine = HttpRequestLine.from(requestLine);

            final StringBuilder requestHeader = new StringBuilder();

            String line;
            while (!Objects.equals(line = reader.readLine(), "")) {
                requestHeader.append(line).append("\r\n");
            }

            HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(requestHeader.toString());


            int contentLength = httpRequestHeader.getContentLength();

            StringBuilder requestBody = new StringBuilder();
            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                int bytesRead = reader.read(buffer, 0, contentLength);
                if (bytesRead > 0) {
                    requestBody.append(buffer, 0, bytesRead);
                }
            }

            HttpRequestBody httpRequestBody = HttpRequestBody.from(requestBody.toString());

            String uri = extractRequestUriFromRequest(requestLine);

            int index = uri.indexOf("?");
            String path;
            String queryString = "";

            int statusCode = 200;
            String statusMessage = "OK";

            if (index != -1) {
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            } else {
                path = uri;
            }

            String setCookie = "";

            if (path.equals("login") && httpRequestLine.isGetMethod()) {
                statusCode = 302;
                statusMessage = "Found";

                HttpCookie cookie = httpRequestHeader.getCookie();


                if (cookie == null || !cookie.existJSESSIONID()) {
                    path = "/login.html";
                } else {
                    String jSessionId = cookie.getJSessionId();
                    Session session = SessionManager.findSession(jSessionId);

                    Optional<User> user = (Optional<User>) session.getAttribute("user");

                    if (user.isPresent()) {
                        path = "/index.html";
                    } else {
                        path = "/login.html";
                    }
                }
            }


            if (path.equals("login") && httpRequestLine.isPostMethod()) {
                statusCode = 302;
                statusMessage = "Found";

                String account = httpRequestBody.getValue("account");
                String password = httpRequestBody.getValue("password");

                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                if (user.isPresent()) {
                    log.info(user.toString());
                    UUID uuid = UUID.randomUUID();
                    Session session = new Session(uuid.toString());
                    session.setAttribute("user", user);
                    SessionManager.add(session);

                    setCookie = "Set-Cookie: JSESSIONID=" + uuid;

                    path = "/index.html";
                } else {
                    log.warn("미가입회원입니다");

                    path = "/401.html";
                }
            }

            if (path.equals("register") && requestHeader.indexOf("POST") != -1) {
                String account = httpRequestBody.getValue("account");
                String password = httpRequestBody.getValue("password");
                String email = httpRequestBody.getValue("email");

                InMemoryUserRepository.save(new User(account, password, email));
                path = "/index.html";
            }

            String contentType = "html";
            if (path.length() != 0) {
                String[] splitedFileName = path.split("\\.");
                if (splitedFileName.length != 1) {
                    contentType = splitedFileName[1];
                }
                if (splitedFileName.length == 1) {
                    path += ".html";
                }
            }

            var responseBody = "";

            if (uri.length() == 0) {
                responseBody = "Hello world!";
            } else {
                final Path filePath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static/" + path)).getPath());

                responseBody = new String(Files.readAllBytes(filePath));
            }

            StatusLine statusLine = StatusLine.from(statusCode, statusMessage);
            HttpResponseHeader responseHeader = new HttpResponseHeader(new LinkedHashMap<>());
            responseHeader.add("Content-Type", "text/" + contentType + ";charset=utf-8");
            responseHeader.add("Content-Length", responseBody.getBytes().length);

            String response = new HttpResponse(statusLine, responseHeader, responseBody).getResponse();

            if (setCookie.length() != 0) {
                response = String.join("\r\n",
                        "HTTP/1.1 " + statusCode + " " + statusMessage + " ",
                        setCookie,
                        "Content-Type: text/" + contentType + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractRequestUriFromRequest(String request) {
        int startIndex = 0;
        if (request.indexOf("GET") != -1) {
            startIndex = request.indexOf("GET ") + 4;
        }
        if (request.indexOf("POST") != -1) {
            startIndex = request.indexOf("POST ") + 5;
        }
        int endIndex = request.indexOf(" HTTP/1.1");

        if (startIndex != -1 && endIndex != -1) {
            return request.substring(startIndex + 1, endIndex);
        }

        return "";
    }
}
