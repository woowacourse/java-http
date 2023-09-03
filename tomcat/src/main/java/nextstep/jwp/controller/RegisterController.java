package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;
import nextstep.jwp.FileIOUtils;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpServlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class RegisterController extends HttpServlet {

    private static final String PREFIX = "static";
    private static final String SUFFIX = ".html";
    private final static Logger LOG = Logger.getGlobal();

    @Override
    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException, URISyntaxException {
        if (req.getQuery() == null) {
            resp.setHttpResponseStartLine(StatusCode.OK);
            Path path = FileIOUtils.getPath(PREFIX + req.getPath() + SUFFIX);
            resp.setResponseBody(Files.readAllBytes(path));
            resp.addHeader("Content-Type", Files.probeContentType(path) + "; charset=utf-8");
        }
    }

    @Override
    public void doPost(final HttpRequest req, final HttpResponse resp) {
        RequestParam requestParam = RequestParam.of(req.getRequestBody());
        Optional<User> findAccount = InMemoryUserRepository.findByAccount(requestParam.get("account"));

        if (!findAccount.isEmpty()) {
            resp.sendRedirect("/401.html");
        } else {
            User user = new User(
                    requestParam.get("account"),
                    requestParam.get("password"),
                    requestParam.get("email")
            );
            InMemoryUserRepository.save(user);
            LOG.info(user.toString());
            resp.sendRedirect("/index.html");
        }
    }
}
