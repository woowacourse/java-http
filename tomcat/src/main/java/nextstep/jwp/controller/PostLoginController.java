package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestBody;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.Controller;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostLoginController implements Controller {

    private final static Logger log = LoggerFactory.getLogger(PostLoginController.class);

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        final HttpRequestBody requestBody = httpRequest.getRequestBody();
        final String account = requestBody.get("account");
        final Optional<User> possibleUser = InMemoryUserRepository.findByAccount(account);

        if (possibleUser.isEmpty()) {
            log.info("존재하지 않는 유저 [요청 account: {}]", account);
            return HttpResponse.init(HttpStatusCode.NOT_FOUND)
                    .setBodyByPath("/404.html");
        }

        final User user = possibleUser.get();
        final String password = requestBody.get("password");
        if (!user.checkPassword(password)) {
            log.info("비밀번호 불일치 [요청 account: {}]", account);
            return HttpResponse.init(HttpStatusCode.UNAUTHORIZED)
                    .setBodyByPath("/401.html");
        }

        final Session session = Session.generate();
        session.setAttribute("user", user);
        SessionManager.add(session);

        log.info("로그인 성공 [요청 account: {}]", account);
        return HttpResponse.init(HttpStatusCode.FOUND)
                .setLocationAsHome()
                .addCookie("JSESSIONID", session.getId());
    }

    @Override
    public boolean isMatch(final HttpRequest httpRequest) {
        final HttpMethod httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();

        return httpMethod.isPost() && path.contains("login");
    }
}
