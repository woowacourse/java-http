package nextstep.jwp.controller;

import static nextstep.jwp.http.StatusCode.NOT_FOUND;
import static nextstep.jwp.utils.FileUtils.getResource;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.Cookie;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.QueryParams;
import nextstep.jwp.http.StatusCode;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.FileUtils;
import org.apache.controller.AbstractController;
import org.apache.coyote.http11.Http11Processor;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ACCOUNT_KEY = "account";

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/register.html")));
    }

    @Override
    protected HttpResponse handlePost(HttpRequest request) {
        return signUp(request);
    }

    private HttpResponse signUp(HttpRequest httpRequest) {
        QueryParams queryParams = httpRequest.getFormData();
        try {
            String account = queryParams.get(ACCOUNT_KEY);
            String email = queryParams.get("email");
            String password = queryParams.get("password");
            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            if (user.isEmpty()) {
                User savedUser = new User(account, password, email);
                InMemoryUserRepository.save(savedUser);
                return HttpResponse.of(StatusCode.CREATED, ContentType.TEXT_HTML,
                    FileUtils.readFile(getResource("/index.html")), setCookie(savedUser));
            }
        } catch (IllegalArgumentException e) {
            log.error("이미 존재하는 회원이거나 모든 파라미터가 입력되지 않아 회원가입에 실패하였습니다.", e);
        }
        return HttpResponse.of(NOT_FOUND, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("404.html")));
    }

    private Cookie setCookie(User user) {
        Session session = SessionManager.generateNewSession();
        session.createAttribute("user", user);
        SessionManager.add(session);
        return Cookie.fromJSessionId(session.getId());
    }
}
