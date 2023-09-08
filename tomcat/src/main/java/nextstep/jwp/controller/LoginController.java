package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;
import org.apache.coyote.response.ResponseStatus;
import org.apache.exception.MethodMappingFailException;
import org.apache.exception.PageRedirectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    public static final String ACCOUNT_KEY = "account";
    public static final String PASSWORD_KEY = "password";

    @Override
    public ResponseEntity handle(final Request request) {
        if (request.isPost()) {
            return login(request);
        }
        if(request.isGet() && request.hasQueryString()){
            return loginInConsole(request);
        }
        if(request.isGet()){
            return loginPage(request);
        }
        throw new MethodMappingFailException();
    }

    private ResponseEntity loginInConsole(final Request request) {
        final String account = request.getQueryValueBy(ACCOUNT_KEY);
        final String password = request.getQueryValueBy(PASSWORD_KEY);

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(MemberNotFoundException::new);
        if (user.checkPassword(password)) {
            log.info("user : {}", user);
        }
        return ResponseEntity.fromViewPath(request.httpVersion(), request.getPath(), ResponseStatus.OK);
    }

    private ResponseEntity login(final Request request) {
        final String account = request.getBodyValue(ACCOUNT_KEY);
        final String password = request.getBodyValue(PASSWORD_KEY);

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(()-> new PageRedirectException.Unauthorized(request.httpVersion()));

        if (user.checkPassword(password)) {
            return ResponseEntity.fromViewPathWithRedirect(request.httpVersion(), request.getPath(), ResponseStatus.MOVED_TEMP, "/index.html");
        }
        throw new PageRedirectException.Unauthorized(request.httpVersion());
    }

    private ResponseEntity loginPage(final Request request) {
        return ResponseEntity.fromViewPath(request.httpVersion(), request.getPath(), ResponseStatus.OK);
    }
}
