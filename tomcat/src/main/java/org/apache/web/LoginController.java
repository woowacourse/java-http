package org.apache.web;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public Http11Response control(final Http11Request request) {
        if (request.getHttpMethod() == HttpMethod.GET) {
            Session session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                return Http11Response.redirect("/index.html", "JSESSIONID=" + session.getId());
            }

//            if (session != null) {
//                User loginUser = session.getAttribute("user");
//                Map<String, String> params = request.extractRequestBodyParams();
//                String account = params.get("account");
//                String password = params.get("password");
//                if (loginUser.getAccount().equals(account) && loginUser.checkPassword(password)) {
//                    return Http11Response.redirect("/index.html", "JSESSIONID="+session.getId());
//                }
//            }
            try {
                String path = request.extractPath();
                URL url = getClass().getClassLoader().getResource(path);
                String body = Files.readString(Paths.get(url.toURI()));
                return Http11Response.ok("text/html;charset=utf-8", body);
            } catch (IOException | URISyntaxException e) {
                log.error("로그인 페이지 읽기 실패", e);
                return Http11Response.serverError();
            }
        }

        final Map<String, String> params = request.extractRequestBodyParams();
        final String account = params.get("account");
        final String password = params.get("password");

        final var loginSuccess = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (loginSuccess.isPresent()) {
            log.info("로그인 성공 - account: {}", account);
            Session session = request.getSession(true);
            session.setAttribute("user", loginSuccess.get());
            String cookieHeader = "JSESSIONID="+session.getId();
            return Http11Response.redirect("/index.html", cookieHeader);
        }
        log.warn("로그인 실패 - account: {}", account);
        return Http11Response.redirect("/401.html");
    }
}
