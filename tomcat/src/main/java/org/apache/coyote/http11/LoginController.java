package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.controller.FrontController;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class LoginController extends FrontController {

    private SessionManager sessionManager = new SessionManager();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String body = request.getBody();
        String[] bodies = body.split("&");
        Map<String, String> params = new HashMap<>();
        for (String value : bodies) {
            String[] keyValue = value.split("=");
            params.put(keyValue[0], keyValue[1]);
        }


        User user = InMemoryUserRepository.findByAccount(params.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        if (!user.checkPassword(params.get("password"))) {
            response.setStatusLine(HttpStatus.UNAUTHORIZED);

            String path = "static/401.html";
            URL url = getClass().getClassLoader().getResource(path);
            String file = new String(Files.readAllBytes(Path.of(url.toURI())));

            response.setHeader("Content-Type", "text/html;charset=utf-8 ");
            response.setBody(file);
        }
        else {
            HttpCookie httpCookie = new HttpCookie();
            httpCookie.addSessionId();

            String cookie = "JSESSIONID=" + httpCookie.getCookieValue("JSESSIONID");
            response.setStatusLine(HttpStatus.FOUND);
            response.setHeader("Location", "/login.html\n");
            response.setHeader("Content-Type", "text/plain");
            response.setHeader("Set-Cookie", cookie);

            Session session = new Session(httpCookie.getCookieValue("JSESSIONID"));
            session.setAttribute("user", user);
            sessionManager.add(session);
            System.out.println(response);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = "static/login.html";
        URL url = getClass().getClassLoader().getResource(path);
        String file = new String(Files.readAllBytes(Path.of(url.toURI())));

        response.setStatusLine(HttpStatus.OK);
        response.setHeader("Content-Type", "text/html;charset=utf-8 ");
        response.setBody(file);
    }
}
