package nextstep.jwp.service;

import static org.apache.coyote.Constants.CRLF;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.NoUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.element.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public boolean login(String uri) {
        if (!isNotQuery(uri)) {
            return false;
        }
        Query query = new Query(uri);
        User user = InMemoryUserRepository.findByAccount(query.find("account"))
                .orElseThrow(NoUserException::new);
        validatePassword(query, user);
        LOG.info("userLogin: " + CRLF + user + CRLF);
        return true;
    }

    private void validatePassword(Query query, User user) {
        if (!user.checkPassword(query.find("password"))) {
            throw new InvalidPasswordException();
        }
    }

    private boolean isNotQuery(String uri) {
        return uri.contains("?");
    }
}
