package org.apache.catalina.servlets;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.apache.catalina.core.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);

        URL resource = getClass().getClassLoader().getResource("static" + req.getRequestURI() + ".html");
        String fileContent = getFileContent(resource);

        resp.setContentType("text/html");
        resp.setContentLength(fileContent.getBytes().length);
        setResponseBody(resp, fileContent);

        loggingUser(req);


    }

    // TODO: 리팩토링 필요 중복 발생
    private String getFileContent(URL resourceURL) throws IOException {
        if (resourceURL != null) {
            File file = new File(resourceURL.getFile());
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String collect = reader.lines().collect(Collectors.joining("\n"));
                return collect + "\n";
            } catch (IOException e) {
                return "Hello world!";
            }
        }
        return "";
    }

    private void setResponseBody(ServletResponse response, String fileContent) {
        HttpResponse httpResponse = (HttpResponse) response;
        httpResponse.setResponseBody(fileContent);
    }

    private void loggingUser(HttpServletRequest req) {
        String account = req.getParameter("account");
        if (account == null) {
            return;
        }

        User user = InMemoryUserRepository.findByAccount(account).orElseThrow(NoSuchElementException::new);
        if (user.checkPassword(req.getParameter("password"))) {
            log.info("user : {}", user);
        }
    }
}
