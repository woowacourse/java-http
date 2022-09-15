package nextstep.jwp.controller;

import jakarta.http.HttpCookie;
import jakarta.http.reqeust.HttpRequest;
import jakarta.http.reqeust.QueryParams;
import jakarta.http.response.HttpResponse;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserLoginException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import jakarta.http.session.Session;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        super.doGet(request, response);
        checkAlreadyLoginUser(request, response);
    }

    private void checkAlreadyLoginUser(final HttpRequest request, final HttpResponse response) {
        Session session = request.getSession();
        if (session != null) {
            response.sendRedirect(INDEX_PAGE_URL);
        }
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        super.doPost(request, response);
        Login(request, response);
    }

    private void Login(final HttpRequest request, final HttpResponse response) {
        QueryParams queryParams = new QueryParams(request.getBody());
        String account = queryParams.find("account");
        String password = queryParams.find("password");

        User user = findUser(account);
        if (!user.checkPassword(password)) {
            throw new UserLoginException("비밀번호가 일치하지 않습니다.");
        }

        Session session = request.setSession();
        session.setAttribute("user", user);
        response.setCookie(HttpCookie.fromJSessionId(session.getId()));
    }

    private User findUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
    }
}
