package nextstep.jwp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import nextstep.jwp.FileIOUtils;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpServlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class LoginController extends HttpServlet {

    private static final String PREFIX = "static";
    private static final String SUFFIX = ".html";

    @Override
    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException {
        if (req.getQuery() == null) {
            if (getUser(req.getSession()) != null) {
                resp.sendRedirect("/index.html");
            } else {
                resp.setHttpResponseStartLine(StatusCode.OK);
                Path path = FileIOUtils.getPath(PREFIX + req.getPath() + SUFFIX);
                resp.setResponseBody(Files.readAllBytes(path));
                resp.addHeader("Content-Type", Files.probeContentType(path) + "; charset=utf-8");
            }
        }
    }

    @Override
    public void doPost(final HttpRequest req, final HttpResponse resp) {
        RequestParam requestParam = RequestParam.of(req.getRequestBody());
        Optional<User> findAccount = InMemoryUserRepository.findByAccount(requestParam.get("account"));

        if (findAccount.isPresent() && findAccount.get().checkPassword(requestParam.get("password"))) {
            final var session = req.getSession(true);
            session.setAttribute("user", findAccount.get());
            resp.sendRedirect("/index.html");
        } else {
            resp.sendRedirect("/401.html");
        }
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }
}
