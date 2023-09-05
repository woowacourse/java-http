package nextstep.jwp.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import nextstep.jwp.FileIOUtils;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpServlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class LoginController extends HttpServlet {

    private static final String PREFIX = "static";
    private static final String SUFFIX = ".html";
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    public void doGet(final HttpRequest req, final HttpResponse resp) throws IOException {
        if (req.getSession().containskey("user")) {
            resp.setHttpResponseStartLine(StatusCode.FOUND);
            resp.sendRedirect("/index.html");
            return;
        }
        resp.setHttpResponseStartLine(StatusCode.OK);
        byte[] file = FileIOUtils.getFileInBytes(PREFIX+req.getPath()+SUFFIX);
        resp.addHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");
        resp.setResponseBody(file);
    }

    @Override
    public void doPost(final HttpRequest req, final HttpResponse resp) {
        try {
            RequestParam requestParam = RequestParam.of(req.getRequestBody());

            Optional<User> findAccount = InMemoryUserRepository.findByAccount(requestParam.get("account"));

            if (findAccount.isPresent()) {
                User user = findAccount.get();
                if (user.checkPassword(requestParam.get("password"))) {
                    Session session = req.getSession();
                    session.setAttribute("user", user);
                    resp.addCookie(JSESSIONID, req.getSession().getId());
                    resp.addCookie("hello", req.getSession().getId());
                    resp.addCookie("bye", req.getSession().getId());
                    resp.addCookie("good", req.getSession().getId());
                    resp.sendRedirect("/index.html");
                    return;
                }
            }
            resp.sendRedirect("/401.html");
        }catch (UnsupportedEncodingException e){
            resp.sendRedirect("/404.html");
        }
    }
}
