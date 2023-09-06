package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.FileIOUtils;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class RegisterController extends AbstractController {

    private static final String PREFIX = "static";
    private static final String SUFFIX = ".html";

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        byte[] file = FileIOUtils.getFileInBytes(PREFIX + request.getPath() + SUFFIX);
        response.setHttpResponseStartLine(StatusCode.OK);
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");
        response.setResponseBody(file);
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        RequestParam requestParam = RequestParam.of(request.getRequestBody());
        Optional<User> findAccount = InMemoryUserRepository.findByAccount(requestParam.get("account"));

        if (findAccount.isPresent()) {
            response.sendRedirect("/401.html");
            return;
        }
        User user = new User(
                requestParam.get("account"),
                requestParam.get("password"),
                requestParam.get("email")
        );
        InMemoryUserRepository.save(user);
        Session session = request.getSession();
        session.setAttribute("user", user);

        response.sendRedirect("/index.html");
    }
}
