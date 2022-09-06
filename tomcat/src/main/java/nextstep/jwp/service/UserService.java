package nextstep.jwp.service;

import static org.apache.coyote.Constants.CRLF;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.NoUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.element.Query;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.apache.coyote.http11.response.element.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.mapping.ResponseEntity;

public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public ResponseEntity login(Query query) {
        try {
            User user = InMemoryUserRepository.findByAccount(query.find("account"))
                    .orElseThrow(NoUserException::new);
            validatePassword(query, user);
            LOG.info("userLogin: " + CRLF + user + CRLF);
            return new ResponseEntity(HttpMethod.GET, "/index.html", HttpStatus.FOUND);
        } catch (NoUserException | InvalidPasswordException e) {
            return new ResponseEntity(HttpMethod.GET, "/401.html", HttpStatus.FOUND);
        }
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
