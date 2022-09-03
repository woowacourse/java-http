package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponse.HttpResponseBuilder;
import org.apache.coyote.support.HttpStatus;

public class UserService {

    public HttpResponse findUser(String account, String password) {
        final var user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            throw new HttpException(HttpStatus.UNAUTHORIZED);
        }
        final var foundUser = user.get();
        if (!foundUser.checkPassword(password)) {
            throw new HttpException(HttpStatus.UNAUTHORIZED);
        }
        return new HttpResponseBuilder(HttpStatus.FOUND).build();
    }
}
