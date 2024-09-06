package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import static org.apache.Method.GET;
import static org.apache.Method.POST;

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpRequest(inputStream);

            String mimeType = "";
            String responseBody = "";
            String response = "";

            if (httpRequest.isMethod(GET)) {
                if (httpRequest.getPath().startsWith("/login")
                        && httpRequest.getCookie() != null
                        && getSession(httpRequest.getCookie()) != null) {
                    Session session = getSession(httpRequest.getCookie());
                    response = getRedirectResponse(session.getId(), responseBody, "/index.html");
                } else {
                    mimeType = httpRequest.getMimeType();
                    responseBody = getResource(httpRequest.getPath());
                    response = getOKResponse(mimeType, responseBody);
                }
            } else if (httpRequest.isMethod(POST)) {
                String requestBody = httpRequest.getRequestBody();

                if (httpRequest.getPath().startsWith("/login")) {
                    Session session = authenticate(requestBody);

                    if (session == null) {
                        response = getRedirectResponse(responseBody, "/401.html");
                    } else {
                        response = getRedirectResponse(session.getId(), responseBody, "/index.html");
                    }
                } else if (httpRequest.getPath().startsWith("/register")) {
                    Session session = register(requestBody);
                    response = getRedirectResponse(session.getId(), responseBody, "/index.html");
                }
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Session authenticate(String requestBody) {
        String account = requestBody.split("&")[0].split("=")[1];
        String password = requestBody.split("&")[1].split("=")[1];

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            String uuid = UUID.randomUUID().toString();
            Session session = new Session(uuid);
            session.setAttribute("user", session);
            SessionManager.add(session);

            return session;
        }

        return null;
    }

    private Session getSession(String cookie) {
        return SessionManager.findSession(cookie);
    }

    private Session register(String requestBody) {
        String account = requestBody.split("&")[0].split("=")[1];
        String password = requestBody.split("&")[1].split("=")[1];
        String email = requestBody.split("&")[2].split("=")[1];

        InMemoryUserRepository.save(new User(account, password, email));
        return authenticate(requestBody);
    }

    private String getResource(String path) throws IOException { // 파일이 아닌 경우도 고려 필요
        URL resource = getClass().getClassLoader().getResource("static" + path);
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String getOKResponse(String mimeType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getRedirectResponse(String responseBody, String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "",
                responseBody);
    }

    private String getRedirectResponse(String sessionId, String responseBody, String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Set-Cookie: JSESSIONID=" + sessionId + " ",
                "Location: " + location + " ",
                "",
                responseBody);
    }
}
