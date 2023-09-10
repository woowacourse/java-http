package org.apache.coyote.controller;

import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> queryParms = Utils.parseToQueryParms(request.getBody());

        try {
            User user = InMemoryUserRepository.findByAccount(queryParms.get("account"))
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));

            if (!user.checkPassword(queryParms.get("password"))) {
                throw new IllegalArgumentException("비밀번호 불일치");
            }
            log.info("user: {}", user);

            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            sessionManager.add(session);
            response.setStatus(FOUND);
            response.setRedirectUrl("/index.html");
            response.setCookie("JSESSIONID=" + session.getId());

        } catch (IllegalArgumentException e) {
            log.error("error : {}", e);
            response.setStatus(FOUND);
            response.setRedirectUrl("/401.html");

        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpCookie cookie = new HttpCookie(request.getHeaders().get("Cookie"));

        String sessionId = cookie.findValue("JSESSIONID");
        if (sessionManager.isExist(sessionId)) {
            response.setStatus(FOUND);
            response.setRedirectUrl("/index.html");
        } else {
            response.setStatus(OK);
            response.setContentType("text/html");
            response.setBody(Utils.readFile("static", "login.html"));
        }
    }
}
