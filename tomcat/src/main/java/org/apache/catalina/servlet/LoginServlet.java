package org.apache.catalina.servlet;

import com.techcourse.controller.LoginController;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;
import org.apache.catalina.servlet.http.request.HttpRequest;
import org.apache.catalina.servlet.http.response.HttpResponse;

public class LoginServlet extends AbstractServlet {

    private static LoginServlet instance;
    private final LoginController loginController = new LoginController();

    private LoginServlet() {
    }

    public static synchronized LoginServlet getInstance() {
        if (instance == null) {
            instance = new LoginServlet();
        }
        return instance;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        loginController.getLoginPage(request, response);
        URL resource = getClass().getClassLoader().getResource("static" + request.getRequestURI() + ".html");
        String fileContent = getFileContent(resource);
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
        loginController.login(request, response);
    }
}
