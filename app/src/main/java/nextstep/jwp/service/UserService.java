package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AlreadyExistUserException;
import nextstep.jwp.exception.InvalidLoingInfoException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserService {

    private static final UserService USER_SERVICE = new UserService();

    private UserService() {
    }

    public static UserService getUserService() {
        return USER_SERVICE;
    }

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public User login(HttpRequest request) {
        String account = request.getParameter("account");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(request.getParameter("password"))) {
            LOG.debug("User Login Success! account: {}", user);
            return user.get();
        }
        LOG.debug("User Login Fail!");
        throw new InvalidLoingInfoException("로그인에 실패하였습니다.");
    }

    public void signUp(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        User user = new User(account, password, email);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            LOG.debug("User Signup Fail! account: {}", account);
            throw new AlreadyExistUserException("이미 존재하는 계정입니다.");
        }
        User savedUser = InMemoryUserRepository.save(user);
        LOG.debug("User Signup Success! account: {}", savedUser);
    }
}
