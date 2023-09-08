package nextstep.jwp.service;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.ResponseDto;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private Session session;

    public ResponseDto checkUserCredentials(String account, String password) {
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if ((user.checkPassword(password)) & session == null) {
                session = new Session(UUID.randomUUID().toString());
                String id = session.getId();

                log.info("user : {}", user);
                return new ResponseDto("302 Found ", "index.html", id);
            }

            if ((user.checkPassword(password))) {
                log.info("user : {}", user);
                return new ResponseDto("302 Found ", "index.html", null);
            }
        }
        return new ResponseDto("401 Unauthorized ", "401.html", null);
    }

    public ResponseDto save(String account, String password, String email) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return new ResponseDto("200 OK ", "index.html", null);
    }
}
