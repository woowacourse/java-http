package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class RegisterController implements RestController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getPath().equals("/register") && request.getMethod() == HttpMethod.POST;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        final var user = new User(
                request.getJsonProperty("account"),
                request.getJsonProperty("password"),
                request.getJsonProperty("email")
        );
        final var headers = HttpHeaders.RestHeaders();
        try {
            InMemoryUserRepository.findByAccount(user.getAccount())
                                  .ifPresentOrElse(
                                          ignored -> {
                                              throw new IllegalArgumentException("이미 존재하는 계정입니다.");
                                          },
                                          () -> InMemoryUserRepository.save(user)
                                  );
            headers.put(HttpHeaders.LOCATION, "/index.html");
            return new HttpResponse(HttpStatusCode.FOUND, headers, "");
        }
        catch (IllegalArgumentException e) {
            headers.put(HttpHeaders.LOCATION, "/register.html");
            return new HttpResponse(HttpStatusCode.FOUND, headers, e.getLocalizedMessage());
        }
    }
}
