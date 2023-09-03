package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import nextstep.jwp.FileIOUtils;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpServlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class LoginController extends HttpServlet {

    private static final String PREFIX = "static";
    private static final String SUFFIX = ".html";

    @Override
    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException, URISyntaxException {
        if (req.getQuery() != null) {

            RequestParam requestParam = RequestParam.of(req.getQuery());
            Optional<User> findAccount = InMemoryUserRepository.findByAccount(requestParam.get("account"));

            if (!findAccount.isEmpty() && findAccount.get().checkPassword(requestParam.get("password"))) {
                resp.sendRedirect("/index.html");
            } else {
                resp.sendRedirect("/401.html");
            }
        } else {
            resp.setHttpResponseStartLine(StatusCode.OK);
            Path path = FileIOUtils.getPath(PREFIX + req.getPath() + SUFFIX);
            resp.setBody(Files.readAllBytes(path));
            resp.addHeader("Content-Type", Files.probeContentType(path) + "; charset=utf-8");
        }
    }
}
