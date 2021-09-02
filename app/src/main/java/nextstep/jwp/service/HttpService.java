package nextstep.jwp.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.constants.UserParams;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;

public class HttpService {
    private static final AtomicLong id = new AtomicLong(1L);

    private HttpService() {
    }

    public static Boolean isAuthorized(Map<String, String> params) {
        User user = InMemoryUserRepository.findByAccount(params.get(UserParams.ACCOUNT))
                .orElseThrow(() -> new UnauthorizedException("해당하는 유저가 없어요"));
        return user.checkPassword(params.get(UserParams.PASSWORD));
    }

    public static void register(Map<String, String> params) {
        validateExistingAccount(params);
        final User user = new User(id.incrementAndGet(), params.get(UserParams.ACCOUNT),
                params.get(UserParams.PASSWORD),
                params.get(UserParams.EMAIL));
        InMemoryUserRepository.save(user);
    }

    private static void validateExistingAccount(Map<String, String> params) {
        Optional<User> account = InMemoryUserRepository.findByAccount(params.get(UserParams.ACCOUNT));
        if (account.isPresent()) {
            throw new BadRequestException("이미 등록된 사용자 입니다.");
        }
    }

}
