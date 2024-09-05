package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Response;

public class SignupRequestHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) throws IOException {
        return httpRequest.getPath().equals("/register")
                && httpRequest.existsBody()
                && "POST".equals(httpRequest.getMethod());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        System.out.println("RERERERER");
        String body = httpRequest.getBody();
        Map<String, String> param = parseBody(body);
        User newUser = new User(param.get("account"), param.get("password"), param.get("email"));
        validateExists(newUser);
        InMemoryUserRepository.save(newUser);
        return Http11Response.builder()
                .protocol(httpRequest.getVersionOfProtocol())
                .statusCode(302)
                .statusMessage("Found")
                .appendHeader("Location", "/index.html")
                .build();
    }

    private Map<String, String> parseBody(String body) {
        Map<String, String> param = new HashMap<>();
        for (String query : body.split("&")) {
            String key = query.split("=")[0];
            String value = query.split("=")[1];
            param.put(key, value);
        }
        return param;
    }

    private void validateExists(User user) {
        if (InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
            throw new UncheckedServletException(new IllegalStateException("이미 존재하는 아이디 입니다."));
        }
    }
}
