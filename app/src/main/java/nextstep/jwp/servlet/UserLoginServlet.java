package nextstep.jwp.servlet;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.tomcat.Servlet;

public class UserLoginServlet extends Servlet {

    public UserLoginServlet() {
        this.requestMappingUri = "/login";
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getParameterValues("account"));
        httpResponse.addStartLine("HTTP/1.1", "302", "Found");
        httpResponse.addContentType("text/html;charset=utf-8");

        if (user.isEmpty() || !user.get().checkPassword(httpRequest.getParameterValues("password"))) {
            httpResponse.addLocation("401.html");
            return;
        }

        httpResponse.addLocation("index.html");
        httpResponse.addBody(user.toString());
        log.debug("로그인 완료! 유저 정보 : {}", user);
    }

}