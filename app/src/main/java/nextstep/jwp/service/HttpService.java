package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.constants.UserParams;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthorizationException;
import nextstep.jwp.model.User;

public class HttpService {

    public Boolean isAuthorized(Map<String, String> params) {
        User user = InMemoryUserRepository.findByAccount(params.get(UserParams.ACCOUNT))
                .orElseThrow(() -> new AuthorizationException("해당하는 유저가 없어요"));
        return user.checkPassword(params.get(UserParams.PASSWORD));
    }

    public void register(Map<String, String> params) {
        final User user = new User(InMemoryUserRepository.size() + 1, params.get(UserParams.ACCOUNT), params.get(UserParams.PASSWORD),
                params.get(UserParams.EMAIL));
        InMemoryUserRepository.save(user);
    }
}
