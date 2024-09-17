package org.apache.catalina.servlet;

import com.techcourse.controller.RegisterController;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;
import org.apache.catalina.servlet.http.request.HttpRequest;
import org.apache.catalina.servlet.http.response.HttpResponse;

public class RegisterServlet extends AbstractServlet {

    private static volatile RegisterServlet instance;
    private final RegisterController registerController = new RegisterController();

    private RegisterServlet() {
    }

    public static RegisterServlet getInstance() {
        if (instance == null) {
            synchronized (RegisterServlet.class) {
                if (instance == null) {
                    instance = new RegisterServlet();
                }
            }
        }
        return instance;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String registerPage = registerController.getRegisterPage();
        URL resource = getClass().getClassLoader().getResource("static" + registerPage + ".html");
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
        registerController.register(request, response);
        response.setContentType("text/html");
    }
}
