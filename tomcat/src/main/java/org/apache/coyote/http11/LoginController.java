package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.exception.CommonException;
import org.apache.catalina.session.Session;

public class LoginController {

    public void login(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {
        String account = httpRequest.getForm("account");
        String password = httpRequest.getForm("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new CommonException(HttpStatus.UNAUTHORIZED));

        if (user.checkPassword(password)) {
            Session session = httpRequest.getSession();
            session.setAttribute("user", user);
            httpResponse.addCookie("SID", session.getId());
            httpResponse.setStatusCode(HttpStatus.FOUND);
            httpResponse.setHeader("Location", "http://localhost:8080");
            System.out.println(account + " 로그인 완료");
            return;
        }
        throw new CommonException(HttpStatus.UNAUTHORIZED);
    }

    public void register(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {

        String account = httpRequest.getForm("account");
        String email = httpRequest.getForm("email");
        String password = httpRequest.getForm("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        httpResponse.setStatusCode(HttpStatus.FOUND);
        httpResponse.setHeader("Location", "http://localhost:8080");
    }
}
