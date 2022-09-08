package nextstep.jwp.ui;

import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.InvalidRequestException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.Session;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String LOGIN_PAGE_PATH = "login.html";
    private static final String REDIRECT_INDEX_PAGE_PATH = "redirect:index.html";
    private static final String REDIRECT_401_PAGE_PATH = "redirect:401.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        Session session = request.getSession();
        ResponseEntity responseEntity = login(session);
        response.initResponseValues(responseEntity);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        ResponseEntity responseEntity = login(request);
        response.initResponseValues(responseEntity);
    }

    private ResponseEntity login(Session session) {
        if (session == null) {
            return ResponseEntity.body(LOGIN_PAGE_PATH);
        }
        if (session.getAttribute("user") != null) {
            return ResponseEntity.body(REDIRECT_INDEX_PAGE_PATH).status(HttpStatus.REDIRECT);
        }
        return ResponseEntity.body(LOGIN_PAGE_PATH);
    }

    private ResponseEntity login(HttpRequest request) {
        try {
            validateUser(request);
        } catch (UserNotFoundException | AuthenticationException | InvalidRequestException e) {
            return ResponseEntity.body(REDIRECT_401_PAGE_PATH).status(HttpStatus.REDIRECT);
        }
        return ResponseEntity.body(REDIRECT_401_PAGE_PATH).status(HttpStatus.REDIRECT);
    }

    private static void validateUser(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        if (requestBody.isEmpty()) {
            throw new InvalidRequestException();
        }
        User user = InMemoryUserRepository.findByAccount(requestBody.get("account"))
                .orElseThrow(UserNotFoundException::new);
        if (!user.checkPassword(requestBody.get("password"))) {
            throw new AuthenticationException();
        }
        Session session = httpRequest.getSession();
        session.setAttribute("user", user);
        log.info(String.format("로그인 성공! 아이디: %s", user.getAccount()));
    }

}
