package nextstep.jwp.controller;

import static org.apache.coyote.support.HttpHeader.CONTENT_TYPE;

import java.util.Optional;
import nextstep.jwp.controller.dto.UserRegisterRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpHeader;
import org.apache.coyote.support.HttpHeaderFactory;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.web.NoBodyResponse;
import org.apache.coyote.web.Response;

public class UserCreateController {

    public Response doPost(final UserRegisterRequest request) {
        Optional<User> user = InMemoryUserRepository.findByAccount(request.getAccount());
        if (user.isPresent()) {
            throw new InvalidUserException("이미 존재하는 계정입니다.");
        }

        InMemoryUserRepository.save(request.toDomain());
        HttpHeaders httpHeaders = HttpHeaderFactory.create(
                new Pair(CONTENT_TYPE.getValue(), ContentType.TEXT_HTML_CHARSET_UTF_8.getValue()),
                new Pair(HttpHeader.LOCATION.getValue(), "index.html")
        );
        return new NoBodyResponse(HttpStatus.FOUND, httpHeaders);
    }
}
