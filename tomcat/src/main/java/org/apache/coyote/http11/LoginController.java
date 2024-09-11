package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    private static final LoginController instance = new LoginController();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private LoginController() {}

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> pairs = request.getBodyQueryString();

        String account = pairs.get("account");
        String password = pairs.get("password");
        if (account != null & password != null & InMemoryUserRepository.doesExistAccount(account)) {
            User user = InMemoryUserRepository.findByAccount(account).get();
            if (user.checkPassword(password)) {
                Session session = new Session(UUID.randomUUID().toString());
                session.addAttribute("user", user);
                sessionManager.add(session);
                redirectToHomeSettingCookie(response, session.getId());
                return;
            }
        }
        redirectTo(response, "/401.html");
    }

    private void redirectToHomeSettingCookie(HttpResponse response, String jSessionId) throws IOException {
        response.addStatusLine("HTTP/1.1 302 Found");
        response.addHeader("Set-Cookie", "JSESSIONID=" + jSessionId);
        response.addHeader("Location", "http://localhost:8080/index.html");
        response.writeResponse();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (doesLoggedIn(request.getCookie())) {
            redirectTo(response, "/index.html");
            return;
        }
        serveStaticFile(request, response);
    }

    private boolean doesLoggedIn(HttpCookie httpCookie) {
        Session session = sessionManager.findSession(httpCookie.get("JSESSIONID"));
        return session != null && session.doesExistAttribute("user");
    }

    private void redirectTo(HttpResponse response, String location) throws IOException {
        response.addStatusLine("HTTP/1.1 302 Found");
        response.addHeader("Location", "http://localhost:8080" + location);
        response.writeResponse();
    }

    private void serveStaticFile(HttpRequest request, HttpResponse response) throws IOException {
        String path = addHtmlExtension(request.getPath());
        String body = getStaticFileContent(path);

        response.addStatusLine("HTTP/1.1 200 OK");
        response.addHeader("Content-Type", "text/" + getFileExtension(path) + ";charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        response.addBody(body);
        response.writeResponse();
    }

    private String addHtmlExtension(String path) {
        if (!"/".equals(path) && !path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private String getStaticFileContent(String path) throws IOException {
        if (Objects.equals(path, "/")) {
            return "Hello world!";
        }
        String staticPath = "static" + path;
        File file = new File(getClass().getClassLoader().getResource(staticPath).getPath());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String getFileExtension(String path) {
        if (Objects.equals(path, "/")) {
            return "html";
        }
        String[] splitPath = path.split("\\.");
        return splitPath[splitPath.length - 1];
    }
}
