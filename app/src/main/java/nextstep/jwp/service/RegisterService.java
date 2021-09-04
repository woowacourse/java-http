package nextstep.jwp.service;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;

public class RegisterService {

    public void registerUser(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (InMemoryUserRepository.isExistAccount(httpRequest.getBodyDataByKey("account"))) {
            httpResponse.redirectWithStatusCode("/register.html", "409");
            return;
        }
        User user = new User(httpRequest.getBodyDataByKey("account"), httpRequest.getBodyDataByKey("password"), httpRequest.getBodyDataByKey("email"));
        InMemoryUserRepository.save(user);
        httpResponse.redirectWithStatusCode("/index.html", "200");
    }
}
