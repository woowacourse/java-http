package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.http.HttpRequest;

public class LoginService {

    public boolean isExistUser(HttpRequest request) {
        return InMemoryUserRepository.existUserByAccountAndPassword(
                request.getParameter("account"),
                request.getParameter("password"));
    }
}
