package nextstep.jwp.service;

import static nextstep.jwp.exception.AuthExceptionType.INVALID_ID_OR_PASSWORD;
import static nextstep.jwp.exception.AuthExceptionType.INVALID_SESSION_ID;
import static nextstep.jwp.exception.AuthExceptionType.USER_NO_EXIST_IN_SESSION;
import static org.apache.coyote.http11.HttpStatus.FOUND;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.common.FormData;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginService {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String INDEX_HTML = "/index.html";

    private final SessionManager sessionManager;

    public LoginService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public HttpResponse loginWithSession(String jsessionid) {
        Session session = sessionManager.findSession(jsessionid)
                .orElseThrow(() -> new AuthException(INVALID_SESSION_ID));
        if (session.getAttribute("user").isEmpty()) {
            throw new AuthException(USER_NO_EXIST_IN_SESSION);
        }
        return HttpResponse.status(FOUND)
                .redirectUri(INDEX_HTML)
                .build();
    }

    public HttpResponse login(HttpRequest httpRequest) {
        FormData formData = FormData.from(httpRequest.getBody());
        String account = formData.get("account");
        String password = formData.get("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            String jsessionid = UUID.randomUUID().toString();
            Session session = new Session(jsessionid);
            session.setAttribute("user", user.get());
            sessionManager.add(session);
            return HttpResponse.status(FOUND)
                    .redirectUri(INDEX_HTML)
                    .cookie(JSESSIONID, jsessionid)
                    .build();
        }
        throw new AuthException(INVALID_ID_OR_PASSWORD);
    }
}
