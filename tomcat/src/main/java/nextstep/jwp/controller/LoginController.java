package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpServlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends HttpServlet {

    private static final String JSESSIONID = "JSESSIONID";

    @Override
    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException {
        if (req.getSession()==null || req.getSession().containskey("user")) {
            resp.sendRedirect("/index.html");
        }
    }

    @Override
    public void doPost(final HttpRequest req, final HttpResponse resp) {
        RequestParam requestParam = RequestParam.of(req.getRequestBody());

        Optional<User> findAccount = InMemoryUserRepository.findByAccount(requestParam.get("account"));

        if (findAccount.isPresent()) {
            User user = findAccount.get();
            if (user.checkPassword(requestParam.get("password"))) {
                Session session = req.getSession();
                session.setAttribute("user", user);
                resp.addCookie(JSESSIONID, req.getSession().getId());
                resp.sendRedirect("/index.html");
                return;
            }
        }
        resp.sendRedirect("/401.html");
    }
}
