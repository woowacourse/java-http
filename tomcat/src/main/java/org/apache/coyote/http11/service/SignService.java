package org.apache.coyote.http11.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.parser.RequestResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class SignService implements HttpService {

    @Override
    public RequestResult doGet(
            Map<String, String> query,
            HttpCookies cookies,
            Session session
    ) throws IOException {
        return new RequestResult(getSignupHtml(), "text/html;charset=utf-8 ");
    }

    private byte[] getSignupHtml() throws IOException {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/register.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        return fileInputStream.readAllBytes();
    }

    @Override
    public RequestResult doPost(
            Map<String, String> query,
            HttpCookies cookies,
            Session session
    ) throws IOException {
        String account = query.get("account");
        String password = query.get("password");
        String email = query.get("email");

        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("회원가입 정보는 null일 수 없습니다.");
        }

        if (InMemoryUserRepository.existByAccount(account)) {
            return new RequestResult(getErrorHtml(), "text/html;charset=utf-8 ");
        }

        InMemoryUserRepository.save(new User(account, password, email));
        return new RequestResult(getRedirectHtml(), "text/html;charset=utf-8 ");
    }

    private byte[] getErrorHtml() throws IOException {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/500.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        return fileInputStream.readAllBytes();
    }

    private byte[] getRedirectHtml() throws IOException {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/index.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        return fileInputStream.readAllBytes();
    }
}
