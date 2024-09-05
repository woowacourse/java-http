package org.apache.catalina.servlets;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;
import org.apache.catalina.core.request.HttpRequest;
import org.apache.catalina.core.response.HttpResponse;
import org.apache.catalina.core.response.HttpStatus;

public class RegisterServlet extends HttpServlet {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + request.getRequestURI() + ".html");
        String fileContent = getFileContent(resource);

        response.setContentType("text/html");
        response.setContentLength(fileContent.getBytes().length);
        response.setResponseBody(fileContent);
    }

    // TODO 뭔가 중복되는 코드 template 패턴으로 바꿔도 좋을 듯
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

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getParameter("account");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        InMemoryUserRepository.save(new User(account, password, email));

        response.setContentType("text/html");
        response.setStatus(HttpStatus.OK.getStatusCode());
        response.sendRedirect("/index.html");
    }
}
