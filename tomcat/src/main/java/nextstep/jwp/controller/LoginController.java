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

public class LoginController extends AbstractController {

    private static final String PREFIX = "static";
    private static final String SUFFIX = ".html";
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.getSession().containskey("user")) {
            response.setHttpResponseStartLine(StatusCode.FOUND);
            response.sendRedirect("/index.html");
            return;
        }
        response.setHttpResponseStartLine(StatusCode.OK);
        byte[] file = FileIOUtils.getFileInBytes(PREFIX+request.getPath()+SUFFIX);
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");
        response.setResponseBody(file);
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        RequestParam requestParam = RequestParam.of(request.getRequestBody());

        Optional<User> findAccount = InMemoryUserRepository.findByAccount(requestParam.get("account"));

        if (findAccount.isPresent()) {
            User user = findAccount.get();
            if (user.checkPassword(requestParam.get("password"))) {
                Session session = request.getSession();
                session.setAttribute("user", user);
                response.addCookie(JSESSIONID, request.getSession().getId());
                response.sendRedirect("/index.html");
                return;
            }
        }
        response.sendRedirect("/401.html");
    }
}
