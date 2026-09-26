package org.apache.coyote.http11;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final HttpRequest request = HttpRequest.from(bufferedReader);
            final String requestMethod = request.getMethod();
            final String path = request.getPath();
            final HttpResponse response = new HttpResponse();

            if ("POST".equals(requestMethod) && "/register".equals(path)) {
                new RegisterController().service(request, response);
            } else if ("POST".equals(requestMethod) && "/login".equals(path)) {
                new LoginController().service(request, response);
            } else if ("GET".equals(requestMethod) && "/login".equals(path) && isLoggedIn(request)) {
                new LoginController().service(request, response);
            } else if ("/".equals(path)) {
                new HomeController().service(request, response);
            } else {
                response.ok(resolveContentType(path), readStaticFile(path));
            }
            outputStream.write(response.format().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isLoggedIn(final HttpRequest request) {
        final HttpCookie cookie = new HttpCookie(request.getHeader("Cookie"));
        final String sessionId = cookie.get("JSESSIONID");

        if (sessionId == null) {
            return false;
        }

        final Session session = SessionManager.INSTANCE.findSession(sessionId);

        if (session == null) {
            return false;
        } else if (session.getAttribute("user") == null) {
            return false;
        } else {
            return true;
        }
    }

    private String readStaticFile(final String path) throws URISyntaxException, IOException {
        final String fileName = "static" + resolveFileName(path);
        final URL url = ClassLoader.getSystemResource(fileName); // 클래스패스에서 static/ 아래 파일을 찾아 실제 위치를 URL로 돌려 줌
        final File file = new File(url.toURI());
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    // 요청 path를 받아서, 서버에서 찾을 파일 이름을 돌려 줌
    private String resolveFileName(final String path) {
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
