package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.Resource;

public class RegisterController extends AbstractController {
    @Override
    public void doPost(Request request, Response response) {
        Map<String, String> requestBody = request.getBody();
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final String email = requestBody.get("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        response.setStatus(HttpStatus.FOUND)
                .setContentType("html")
                .setLocation("index.html")
                .setResponseBody(Resource.getFile("index.html"));
    }


    @Override
    protected void doGet(Request request, Response response) {
        response.setStatus(HttpStatus.OK)
                .setContentType("html")
                .setResponseBody(Resource.getFile("register.html"));
    }
}
