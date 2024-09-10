package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RegisterController implements Controller {

    @Override
    public HttpResponse process(HttpRequest request) {
        if (request.isGetMethod()) {
            String uri = request.getUri();
            return HttpResponse.of(uri + ".html", HttpStatusCode.OK);
        }
        if (request.isPostMethod()) {
            String body = request.getBody();
            String decodedBody = URLDecoder.decode(body, StandardCharsets.UTF_8);
            String[] parts = decodedBody.split("&");
            String account = parts[0].split("=")[1];
            String email = parts[1].split("=")[1];
            String password = parts[2].split("=")[1];
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            return HttpResponse.of("/index.html", HttpStatusCode.OK);
        }
        return null;
    }
}
