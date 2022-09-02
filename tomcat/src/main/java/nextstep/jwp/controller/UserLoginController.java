package nextstep.jwp.controller;

import static org.apache.coyote.support.HttpHeader.CONTENT_TYPE;

import nextstep.jwp.controller.dto.UserLoginRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpHeader;
import org.apache.coyote.support.HttpHeaderFactory;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.web.Cookie;
import org.apache.coyote.web.NoBodyResponse;
import org.apache.coyote.web.Response;
import org.apache.coyote.web.Session;
import org.apache.coyote.web.SessionManager;

public class UserLoginController {

    public Response doGet(final UserLoginRequest request) {
        User user = InMemoryUserRepository.findByAccount(request.getAccount())
                .orElseThrow(UserNotFoundException::new);
        if (user.checkPassword(request.getPassword())) {
            HttpHeaders httpHeaders = HttpHeaderFactory.create(
                    new Pair(CONTENT_TYPE.getValue(), ContentType.TEXT_HTML_CHARSET_UTF_8.getValue()),
                    new Pair(HttpHeader.LOCATION.getValue(), "/index.html")
            );
            Response response = new NoBodyResponse(HttpStatus.FOUND, httpHeaders);
            Cookie cookie = SessionManager.createCookie();
            Session session = new Session(cookie.getKey());
            session.setAttribute("user", user);
            response.addCookie(cookie);
            return response;
        }
        HttpHeaders httpHeaders = HttpHeaderFactory.create(
                new Pair(CONTENT_TYPE.getValue(), ContentType.TEXT_HTML_CHARSET_UTF_8.getValue()),
                new Pair(HttpHeader.LOCATION.getValue(), "/401.html")
        );
        return new NoBodyResponse(HttpStatus.UNAUTHORIZED, httpHeaders);
    }
}
