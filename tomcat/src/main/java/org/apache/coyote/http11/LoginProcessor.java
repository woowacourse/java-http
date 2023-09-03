package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.requests.HttpCookie;
import org.apache.coyote.requests.RequestBody;
import org.apache.coyote.requests.RequestHeader;

import java.io.IOException;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;

public class LoginProcessor {

    private LoginProcessor() {
    }

    public static HttpResponse doLogin(RequestHeader requestHeader, RequestBody requestBody) throws IOException {
        String query = requestBody.getItem();
        if (query.isBlank()) {
            return ViewResolver.resolveView("/login");
        }

        String account = "";
        String password = "";

        for (String parameter : query.split("&")) {
            int idx = parameter.indexOf("=");
            String key = parameter.substring(0, idx);
            String value = parameter.substring(idx + 1);
            if ("account".equals(key)) {
                account = value;
            }
            if ("password".equals(key)) {
                password = value;
            }
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseGet(() -> null);

        if (user != null && user.checkPassword(password)) {
            HttpResponse httpResponse = new HttpResponse("/index.html", HttpStatus.FOUND, TEXT_HTML);
            System.out.println("로그인 성공!" + user);
            String cookies = requestHeader.get("Cookie");
            HttpCookie cookie = HttpCookie.from(cookies);
            if (cookie.get("JSESSIONID") == null) {
                httpResponse.addHeader("Set-Cookie", cookie.generateCookie());
            }
            return httpResponse;
        }

        return ViewResolver.resolveView("/401.html");
    }


    public static HttpResponse doRegister(RequestBody requestBody) throws IOException {
        String query = requestBody.getItem();
        if (query.isBlank()) {
            return ViewResolver.resolveView("/register");
        }

        String account = "";
        String password = "";
        String email = "";

        for (String parameter : query.split("&")) {
            int idx = parameter.indexOf("=");
            String key = parameter.substring(0, idx);
            String value = parameter.substring(idx + 1);

            if ("account".equals(key)) {
                account = value;
            }
            if ("password".equals(key)) {
                password = value;
            }
            if ("email".equals(key)) {
                email = value;
            }
        }

        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            return ViewResolver.resolveView("/register");
        }

        User registUser = new User(account, password, email);
        InMemoryUserRepository.save(registUser);
        System.out.println("회원가입 성공! " + registUser);

        return ViewResolver.resolveView("/index.html");
    }
}
