package org.apache.catalina;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class RegisterHandler {

    public HttpResponse handle(HttpRequest request) {
        if (request.getMethod().equals("GET")) {
            return new HttpResponse(HttpStatusCode.OK, ContentType.HTML, "/register.html");
        }
        if (request.getMethod().equals("POST")) {
            return register(request);
        }
        return new HttpResponse(HttpStatusCode.NOT_FOUND, ContentType.HTML, "/404.html"); // TODO: 405 페이지 필요
    }

    private HttpResponse register(HttpRequest request) {
        User user = createNewUser(request);
        InMemoryUserRepository.save(user);
        log.info("로그인 성공! 아이디 : {}", user.getAccount());
        return new HttpResponse(HttpStatusCode.FOUND, ContentType.HTML, "/index.html");
    }

    private User createNewUser(HttpRequest request) {
        Map<String, String> requestBody = request.parseQueryStringForm(request.getBody());
        Long id = InMemoryUserRepository.getNextId();
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");
        return new User(id, account, password, email);
    }
}
