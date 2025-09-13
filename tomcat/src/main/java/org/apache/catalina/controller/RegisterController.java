package org.apache.catalina.controller;

import static org.apache.coyote.http11.Mime.HTML;
import static org.apache.coyote.http11.Mime.JSON;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.util.FileReader;
import org.apache.coyote.http11.util.HttpRequestParser;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final var params = HttpRequestParser.parseQueryString(request.getBody());

        final String account = params.get("account");
        final String password = params.get("password");
        final String email = params.get("email");

        if (account == null || password == null || email == null
                || account.isBlank() || password.isBlank() || email.isBlank()
        ) {
            response.setContentType(JSON.getType());
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBody("값이 모두 입력되지 않았습니다.");
            response.send();
            return;
        }

        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.setContentType(HTML.getType());
        response.setStatus(HttpStatus.OK);
        response.setBody(FileReader.readByName("index.html"));
        response.send();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(HTML.getType());
        response.setStatus(HttpStatus.OK);
        response.setBody(FileReader.readByName("register.html"));
        response.send();
    }
}
