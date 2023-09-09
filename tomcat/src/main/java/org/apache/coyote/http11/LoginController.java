package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;
import static org.apache.coyote.http11.HttpStatus.UNAUTHORIZED;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class LoginController extends AbstractController {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String INDEX_HTML = "/index.html";

    private final ResourceLoader resourceLoader;
    private final SessionManager sessionManager;

    public LoginController(ResourceLoader resourceLoader, SessionManager sessionManager) {
        this.resourceLoader = resourceLoader;
        this.sessionManager = sessionManager;
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        Optional<String> jsessionid = request.getCookie(JSESSIONID);
        if (jsessionid.isPresent()) {
            return loginWithSession(jsessionid.get());
        }
        return login(request);
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        Optional<String> jsessionid = request.getCookie(JSESSIONID);
        if (jsessionid.isPresent()) {
            return loginWithSession(jsessionid.get());
        }
        return HttpResponse.status(OK)
                .body(resourceLoader.load("static/login.html"))
                .contentType(TEXT_HTML)
                .build();
    }

    private HttpResponse loginWithSession(String jsessionid) {
        Session session = sessionManager.findSession(jsessionid)
                .orElseThrow(() -> new HttpException(UNAUTHORIZED, "잘못된 세션 아이디입니다"));
        if (session.getAttribute("user").isEmpty()) {
            throw new HttpException(UNAUTHORIZED, "세션에 회원정보가 존재하지 않습니다");
        }
        return HttpResponse.status(FOUND)
                .redirectUri(INDEX_HTML)
                .build();
    }

    private HttpResponse login(HttpRequest httpRequest) {
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
        throw new HttpException(UNAUTHORIZED, "아이디나 비밀번호를 확인해주세요");
    }
}
