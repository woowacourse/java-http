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

import static org.apache.coyote.http11.Method.GET;
import static org.apache.coyote.http11.Method.POST;
import static org.apache.coyote.http11.Status.*;

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
            HttpResponse httpResponse = new HttpResponse();

            if (httpRequest.isMethod(GET)) {
                if (httpRequest.getPath().startsWith("/login")
                        && httpRequest.getCookie() != null
                        && getSession(httpRequest.getCookie()) != null) {
                    Session session = getSession(httpRequest.getCookie());

                    httpResponse.setStatusLine(FOUND);
                    httpResponse.setCookie(session.getId());
                    httpResponse.setLocation("/index.html");
                } else {
                    String responseBody = getResource(httpRequest.getPath());

                    httpResponse.setStatusLine(OK);
                    httpResponse.setContentType(httpRequest.getContentType());
                    httpResponse.setResponseBody(responseBody);
                    httpResponse.setContentLength(responseBody.getBytes().length);
                }
            } else if (httpRequest.isMethod(POST)) {
                String requestBody = httpRequest.getRequestBody();

                if (httpRequest.getPath().startsWith("/login")) {
                    Session session = authenticate(requestBody);

                    if (session == null) {
                        httpResponse.setStatusLine(FOUND);
                        httpResponse.setLocation("/401.html");
                    } else {
                        httpResponse.setStatusLine(FOUND);
                        httpResponse.setCookie(session.getId());
                        httpResponse.setLocation("/index.html");
                    }
                } else if (httpRequest.getPath().startsWith("/register")) {
                    Session session = register(requestBody);

                    httpResponse.setStatusLine(FOUND);
                    httpResponse.setCookie(session.getId());
                    httpResponse.setLocation("/index.html");
                }
            }

            outputStream.write(httpResponse.getResponse().getBytes());
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
}
