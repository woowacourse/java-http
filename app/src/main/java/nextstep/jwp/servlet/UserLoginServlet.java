package nextstep.jwp.servlet;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.reponse.HttpResponse;
import nextstep.jwp.http.reponse.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.model.User;
import nextstep.jwp.tomcat.Servlet;

public class UserLoginServlet extends Servlet {

    public UserLoginServlet() {
        super("/login");
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.addFile("/login.html");
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getParameter("account"));
        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setHeader("Content-Type", "text/html;charset=utf-8");

        if (checkUser(httpRequest, user)) {
            httpResponse.setHeader("Location", "401.html");
            return;
        }

        log.debug("로그인 완료! 유저 정보 : {}", user);
        httpResponse.setHeader("Location", "index.html");
    }

    private boolean checkUser(HttpRequest httpRequest, Optional<User> user) {
        return user.isEmpty() || !user.get().checkPassword(httpRequest.getParameter("password"));
    }

}