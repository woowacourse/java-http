package org.apache.catalina.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;
import org.apache.catalina.servlet.http.Cookie;
import org.apache.catalina.servlet.http.Session;
import org.apache.catalina.servlet.http.SessionManager;
import org.apache.catalina.servlet.http.request.HttpRequest;
import org.apache.catalina.servlet.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends AbstractServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    private static LoginServlet instance;

    private LoginServlet() {}

    public static LoginServlet getInstance() {
        if (instance == null) {
            instance = new LoginServlet();
        }
        return instance;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        URL resource = getClass().getClassLoader().getResource("static" + request.getRequestURI() + ".html");
        String fileContent = getFileContent(resource);

        if (request.hasCookie()) {
            Cookie cookie = request.getCookie();
            SessionManager sessionManager = SessionManager.getInstance();
            if (sessionManager.hasSession(cookie.getValue())) {
                response.sendRedirect("/index.html");
            }
        }

        response.setContentType("text/html");
        response.setContentLength(fileContent.getBytes().length);
        response.setResponseBody(fileContent);
    }

    private String getFileContent(URL resourceURL) {
        if (resourceURL == null) {
            return "";
        }
        File file = new File(resourceURL.getFile());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String collect = reader.lines().collect(Collectors.joining("\n"));
            return collect + "\n";
        } catch (IOException e) {
            return "Hello world!";
        }
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(user -> login(request, response, user), () -> response.sendRedirect("/401.html"));
    }

    private void login(HttpRequest request, HttpResponse response, User user) {
        if (user.checkPassword(request.getParameter("password"))) {
            log.info("user : {}", user);
            Session session = request.getSession();
            session.setAttribute("user", user);
            response.addCookie(new Cookie("JSESSIONID", session.getId()));
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/401.html");
    }
}
