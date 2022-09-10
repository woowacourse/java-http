package org.apache.coyote.controller;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.response.HttpResponse;
import org.apache.coyote.domain.response.RedirectUrl;
import org.apache.coyote.domain.response.ResponseBody;
import org.apache.coyote.domain.response.statusline.ContentType;
import org.apache.coyote.domain.response.statusline.HttpStatusCode;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String URL = "/login";
    private static final String INDEX_HTML = "/index.html";
    private static final String USER_SESSION_KEY = "user";
    private static final String HTML_401_PAGE = "/401.html";
    private static final String PASSWORD_KEY = "password";
    private static final String ACCOUNT_KEY = "account";

    @Override
    void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.hasJSESSIONID() && request.checkSession()) {
            response.responseLine(request.getHttpVersion(), HttpStatusCode.FOUND)
                    .header(RedirectUrl.from(INDEX_HTML));
            log.info("[Login Controller] doGet - Exist Session ");
            return;
        }
        response.responseLine(request.getHttpVersion(), HttpStatusCode.OK)
                .header(ContentType.from(request.getFilePath()))
                .responseBody(ResponseBody.from(request.getFilePath()));
        log.info("[Login Controller] doGet - Not Exist Session ");
    }

    @Override
    void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Optional<User> user = InMemoryUserRepository.findByAccount(request.getBodyValue(ACCOUNT_KEY));
        if (user.isPresent()) {
            log.info(user.get().toString());
            login(user.get(), request, response);
            return;
        }
        response.responseLine(request.getHttpVersion(), HttpStatusCode.FOUND)
                .header(RedirectUrl.from(HTML_401_PAGE))
                .header(ContentType.from(request.getFilePath()))
                .header(request.getHttpCookie());
        log.info("[Login Controller] doPost - Not Found User Account ");
    }

    private void login(User user, HttpRequest request, HttpResponse response) {
        if (user.checkPassword(request.getBodyValue(PASSWORD_KEY))) {
            Session session = request.getSession();
            session.setAttribute(USER_SESSION_KEY, user);
            SessionManager.add(session);
            request.getHttpCookie().add(session);
            response.responseLine(request.getHttpVersion(), HttpStatusCode.FOUND)
                    .header(RedirectUrl.from(INDEX_HTML))
                    .header(ContentType.from(request.getFilePath()))
                    .header(request.getHttpCookie());
            log.info("[Login Controller] doPost - User Session Create & Login ");
            return;
        }
        log.info("[Login Controller] doPost - Login Failed(Invalid Password)");
    }

    @Override
    public boolean handle(HttpRequest httpRequest) {
        return URL.equals(httpRequest.getUri());
    }
}
