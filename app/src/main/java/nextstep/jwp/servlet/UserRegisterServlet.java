package nextstep.jwp.servlet;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.reponse.HttpResponse;
import nextstep.jwp.http.reponse.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.model.User;
import nextstep.jwp.tomcat.Servlet;

public class UserRegisterServlet extends Servlet {

    public UserRegisterServlet() {
        this.requestMappingUri = "/register";
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.addFile("/register.html");
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = new User(
            httpRequest.getParameter("account"),
            httpRequest.getParameter("password"),
            httpRequest.getParameter("email")
        );

        InMemoryUserRepository.save(user);
        log.info("회원 가입 완료! 유저 정보 : {}", user);

        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.setHeader("Location", "index.html");
    }

}
