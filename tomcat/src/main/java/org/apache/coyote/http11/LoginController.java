package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        // GET /login 처리 (이미 로그인된 세션이 있으면 /index.html로 리다이렉트)
        String jsessionId = request.getHttpCookies().getCookie("JSESSIONID");
        Session session = SessionManager.findSession(jsessionId);

        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        // POST /login 처리 (계정/비번 검증 후 세션 저장 및 302 리다이렉트)
        String account = request.getParameters().get("account");
        String password = request.getParameters().get("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            String jsessionId = request.getHttpCookies().getCookie("JSESSIONID");
            Session session = SessionManager.findSession(jsessionId);
            if (session == null) {
                session = new Session(jsessionId);
                SessionManager.add(session);
            }
            session.setAttribute("user", user.get());
            response.sendRedirect("/index.html");
        } else {
            response.sendRedirect("/401.html");
        }
    }
}
