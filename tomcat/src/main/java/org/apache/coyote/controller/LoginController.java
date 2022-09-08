package org.apache.coyote.controller;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.response.ContentType;
import org.apache.coyote.domain.response.HttpResponse;
import org.apache.coyote.domain.response.HttpStatusCode;
import org.apache.coyote.domain.response.RedirectUrl;
import org.apache.coyote.domain.response.ResponseBody;
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
        if (request.getHttpCookie().hasJSESSIONID() && request.checkSession()) {
            response.responseLine(request.getRequestLine().getHttpVersion(), HttpStatusCode.FOUND)
                    .header(RedirectUrl.from(INDEX_HTML));
            log.info("[Login Controller] doGet - Exist Session ");
            return;
        }
        response.responseLine(request.getRequestLine().getHttpVersion(), HttpStatusCode.OK)
                .header(ContentType.from(request.getRequestLine().getPath().getFilePath()))
                .responseBody(ResponseBody.from(request.getRequestLine().getPath().getFilePath()));
        log.info("[Login Controller] doGet - Not Exist Session ");
    }

    @Override
    void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Optional<User> user = InMemoryUserRepository.findByAccount(
                request.getRequestBody().getRequestBody().get(ACCOUNT_KEY));
        if (user.isPresent()) {
            log.info(user.get().toString());
            if (user.get().checkPassword(request.getRequestBody().getRequestBody().get(PASSWORD_KEY))) {
                Session session = request.getSession();
                session.setAttribute(USER_SESSION_KEY, user);
                SessionManager.add(session);
                request.getHttpCookie().add(session);
                response.responseLine(request.getRequestLine().getHttpVersion(), HttpStatusCode.FOUND)
                        .header(RedirectUrl.from(INDEX_HTML))
                        .header(ContentType.from(request.getRequestLine().getPath().getFilePath()))
                        .header(request.getHttpCookie());
                log.info("[Login Controller] doPost - User Session Create & Login ");
                return;
            }
        }
        response.responseLine(request.getRequestLine().getHttpVersion(), HttpStatusCode.FOUND)
                .header(RedirectUrl.from(HTML_401_PAGE))
                .header(ContentType.from(request.getRequestLine().getPath().getFilePath()))
                .header(request.getHttpCookie());
        log.info("[Login Controller] doPost - Login Failed ");
    }

    @Override
    public boolean handle(HttpRequest httpRequest) {
        return URL.equals(httpRequest.getRequestLine().getPath().getPath());
    }
}
