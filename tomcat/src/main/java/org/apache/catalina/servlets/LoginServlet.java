package org.apache.catalina.servlets;

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
import java.util.stream.Collectors;
import org.apache.catalina.core.HttpResponse;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        URL resource = getClass().getClassLoader().getResource("static" + req.getRequestURI() + ".html");
        String fileContent = getFileContent(resource);

        resp.setContentType("text/html");
        resp.setContentLength(fileContent.getBytes().length);
        setResponseBody(resp, fileContent);
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
}
