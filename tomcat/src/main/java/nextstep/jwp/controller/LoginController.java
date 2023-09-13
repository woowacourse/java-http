package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemorySession;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.Resource;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(Request request, Response response) {
        Map<String, String> body = request.getBody();
        User user = InMemoryUserRepository.findByAccount(body.get("account"))
                .orElseThrow(() -> new UnauthorizedException("해당 유저가 없습니다."));
        if (!user.checkPassword(body.get("password"))) {
            throw new UnauthorizedException("아이디 및 패스워드가 틀렸습니다.");
        }
        String jSessionId = InMemorySession.login(user);
        Map<String, String> cookie = new HashMap<>();
        if (!request.getCookie().containsKey("JSESSIONID")) {
            cookie.put("JSESSIONID", jSessionId);
        }
        response.setStatus(HttpStatus.FOUND)
                .setContentType("html")
                .setCookie(cookie)
                .setLocation("index.html")
                .setResponseBody(Resource.getFile("index.html"));
    }

    @Override
    protected void doGet(Request request, Response response) {
        response.setStatus(HttpStatus.OK)
                .setContentType("html")
                .setResponseBody(Resource.getFile("login.html"));
    }
}
