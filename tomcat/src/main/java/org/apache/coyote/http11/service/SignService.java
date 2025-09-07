package org.apache.coyote.http11.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.parser.ContentParseResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class SignService implements HttpService {

    @Override
    public ContentParseResult doGet(Map<String, String> query) throws IOException {
        return new ContentParseResult(getSignupHtml(), "text/html;charset=utf-8 ");
    }

    private byte[] getSignupHtml() throws IOException {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/register.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        return fileInputStream.readAllBytes();
    }

    @Override
    public ContentParseResult doPost(Map<String, String> query) throws IOException {
        String account = query.get("account");
        String password = query.get("password");
        String email = query.get("email");

        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("회원가입 정보는 null일 수 없습니다.");
        }

        if (InMemoryUserRepository.existByAccount(account)) {
            return new ContentParseResult(getErrorHtml(), "text/html;charset=utf-8 ");
        }

        InMemoryUserRepository.save(new User(account, password, email));
        return new ContentParseResult(getRedirectHtml(), "text/html;charset=utf-8 ");
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
