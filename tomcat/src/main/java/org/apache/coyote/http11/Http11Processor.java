package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.sessionManager = new SessionManager();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()
        ) {
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            HttpRequestMessageParser headerParser = new HttpRequestMessageParser(bufferedReader);
            HttpRequestMessage httpRequestMessage = headerParser.parseRequestMessage();

            if (httpRequestMessage.isNull()) {
                return;
            }
            String[] startLineElements = httpRequestMessage.getStartLine().split(" ");
            String response = null;
            if (startLineElements[0].equals("POST")) {
                response = makePostMethodResponse(httpRequestMessage);
            }
            if (startLineElements[0].equals("GET")) {
                response = makeGetMethodResponse(httpRequestMessage);
            }
            if (response == null) {
                return;
            }

            outputStream.write(response.getBytes());
            outputStream.flush();

            inputStreamReader.close();
            bufferedReader.close();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makePostMethodResponse(final HttpRequestMessage httpRequestMessage) {
        String requestBody = httpRequestMessage.getBody();
        Map<String, String> queryStorage = new HashMap<>();

        for (String query : requestBody.split("&")) {
            String[] keyValue = query.split("=");
            queryStorage.put(keyValue[0], keyValue[1]);
        }

        String requestTarget = httpRequestMessage.getStartLine().split(" ")[1];
        String redirectTarget = "/index.html";
        String uuid = "";
        boolean loginSuccess = false;
        if (requestTarget.equals("/register")) {
            final User user = new User(queryStorage.get("account"), queryStorage.get("password"), queryStorage.get("email"));
            InMemoryUserRepository.save(user);
        }
        if (requestTarget.equals("/login")) {
            User user = InMemoryUserRepository.findByAccount(queryStorage.get("account"))
                    .orElseThrow(() -> new IllegalArgumentException("해당 account가 존재하지 않습니다."));
            if (user.checkPassword(queryStorage.get("password"))) {
                log.info("user : {}", user);
                loginSuccess = true;
                final String userUuid = SessionGenerator.generateUUID().toString();
                Session session = new Session(userUuid);
                session.setAttribute("user", user);
                sessionManager.add(session);
                uuid = userUuid;
            }
            else {
                redirectTarget = "/401.html";
           }
        }
        boolean noSession = false;
        final Map<String, String> headerInfo = httpRequestMessage.getHeaders();
        if (headerInfo.containsKey("Cookie")) {
            final HttpCookie httpCookie = new HttpCookie(headerInfo.get("Cookie"));
            if (!httpCookie.existSessionId("JSESSIONID")) {
                final UUID newUuid = SessionGenerator.generateUUID();
                sessionManager.add(new Session(newUuid.toString()));
                uuid = newUuid.toString();
                noSession = true;
            }
        }

        String statusCode = "302 Found";

        var headers = String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ");
        headers += "\r\n" + "Location: " + redirectTarget + " ";

        if (loginSuccess || noSession) {
            headers += "\r\n" + "Set-Cookie: JSESSIONID=" + uuid;
        }

        return String.join("\r\n",
                headers,
                "");
    }

    private String makeGetMethodResponse(final HttpRequestMessage httpRequestMessage) throws URISyntaxException, IOException {
        String statusCode = "200 OK";

        final Map<String, String> headerInfo = httpRequestMessage.getHeaders();

        String requestTarget = httpRequestMessage.getStartLine().split(" ")[1];

        String redirectTarget = "";
        if (requestTarget.equals("/login") && headerInfo.containsKey("Cookie")) {
            final HttpCookie httpCookie = new HttpCookie(headerInfo.get("Cookie"));
            if (httpCookie.existSessionId("JSESSIONID")) {
                String sessionId = httpCookie.getValue("JSESSIONID");
                if (sessionManager.isExistSessionId(sessionId)) {
                    redirectTarget = "/index.html";
                    statusCode = "302 Found";
                }
            }
        }

        String responseBody = makeResponseBody(requestTarget);
        if (responseBody == null) {
            return null;
        }
        String contentType = makeContentType(requestTarget);

        var headers = String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ");

        if (!requestTarget.isBlank()) {
            headers += "\r\n" + "Location: " + redirectTarget + " ";
        }

        return String.join("\r\n",
                headers,
                "",
                responseBody);
    }

    private String makeContentType(final String requestTarget) {
        if (requestTarget.length() > 3 && requestTarget.endsWith("css")) {
                return "text/css";
            }
        return "text/html";
    }

    private String makeResponseBody(final String requestTarget) throws URISyntaxException, IOException {
        if (requestTarget.equals("/")) {
            return "Hello world!";
        }

        String fileExtension = makefileExtension(requestTarget);
        final URL resource = Http11Processor.class.getResource("/static" + fileExtension);
        if (resource == null) {
            return null;
        }
        final Path path = Paths.get(resource.toURI()).toFile().toPath();
        return Files.readString(path);
    }

    private String makefileExtension(final String requestTarget) {
        if (requestTarget.equals("/login")) {
            return "/login.html";
        }
        if (requestTarget.equals("/register")) {
            return "/register.html";
        }
        return requestTarget;
    }
}
