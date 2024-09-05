package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Method;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;

public class SignupRequestHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) throws IOException {
        return httpRequest.getPath().equals("/register")
                && httpRequest.existsBody()
                && Http11Method.POST.equals(httpRequest.getMethod());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        Map<String, String> param = httpRequest.getParsedBody();
        User newUser = new User(param.get("account"), param.get("password"), param.get("email"));
        validateExists(newUser);
        InMemoryUserRepository.save(newUser);
        return Http11Response.builder()
                .status(HttpStatus.FOUND)
                .appendHeader("Location", "/index.html")
                .build();
    }

    private void validateExists(User user) {
        if (InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
            throw new UncheckedServletException(new IllegalStateException("이미 존재하는 아이디 입니다."));
        }
    }
}
