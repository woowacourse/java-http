package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicateException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterService.class);

    private final InMemoryUserRepository inMemoryUserRepository;

    public RegisterService(InMemoryUserRepository inMemoryUserRepository) {
        this.inMemoryUserRepository = inMemoryUserRepository;
    }

    public void register(User user) {
        validateNotDuplicate(user);
        inMemoryUserRepository.save(user);
    }

    private void validateNotDuplicate(User user) {
        validateAccountNotDuplicate(user.getAccount());
        validateEmailNotDuplicate(user.getEmail());
    }

    private void validateAccountNotDuplicate(String account) {
        if (inMemoryUserRepository.existsByAccount(account)) {
            LOG.debug("회원 등록 실패 : account 중복 => account: {}", account);
            throw new DuplicateException("이미 존재하는 account 입니다.");
        }
    }

    private void validateEmailNotDuplicate(String email) {
        if (inMemoryUserRepository.existsByEmail(email)) {
            LOG.debug("회원 등록 실패 : email 중복 => email: {}", email);
            throw new DuplicateException("이미 존재하는 email 입니다.");
        }
    }
}
