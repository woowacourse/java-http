package org.apache.coyote;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RegisterController extends AbstractController{


    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String body = request.getBody();
        String[] bodies = body.split("&");
        Map<String, String> params = new HashMap<>();
        for (String value : bodies) {
            String[] keyValue = value.split("=");
            params.put(keyValue[0], keyValue[1]);
        }
        User user = new User(params.get("account"), params.get("password"), params.get("email"));
        InMemoryUserRepository.save(user);
        System.out.println("회원가입 성공: " + user.getAccount());

        String path = "static/index.html";
        URL url = getClass().getClassLoader().getResource(path);
        String file = new String(Files.readAllBytes(Path.of(url.toURI())));


        response.setStatusLine(HttpStatus.OK);
        response.setHeader("Content-Type", "text/html;charset=utf-8 ");
        response.setBody(file);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = "static/register.html";
        URL url = getClass().getClassLoader().getResource(path);
        String file = new String(Files.readAllBytes(Path.of(url.toURI())));

        response.setStatusLine(HttpStatus.OK);
        response.setHeader("Content-Type", "text/html;charset=utf-8 ");
        response.setBody(file);
    }
}
