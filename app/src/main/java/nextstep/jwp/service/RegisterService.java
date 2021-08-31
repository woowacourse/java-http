package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private final Logger log = LoggerFactory.getLogger(RegisterService.class);

    public void register(RequestBody body) {
        String account = body.getParam("account");
        String password = body.getParam("password");
        String email = body.getParam("email");

        User user = new User(InMemoryUserRepository.getUserId(), account, password, email);
        InMemoryUserRepository.save(user);
        log.debug("{} 님이 회원가입 하였습니다.", user.getAccount());
    }

}
