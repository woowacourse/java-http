package org.apache.coyote.http11.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.parser.RequestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class UserService implements HttpService {

    private static final Logger log = LoggerFactory.getLogger(HttpService.class);

    public RequestResult doGet(
            Map<String, String> query,
            HttpCookies cookies,
            Session session
    ) throws IOException {
        if (session.getAttribute("user") != null) {
            return new RequestResult(getRedirectHtml(), "text/html;charset=utf-8 ");
        }
        return new RequestResult(getLoginHtml(), "text/html;charset=utf-8 ");
    }

    private byte[] getAuthorizationFailHtml() throws IOException {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/401.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        return fileInputStream.readAllBytes();
    }

    private byte[] getRedirectHtml() throws IOException {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/index.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        return fileInputStream.readAllBytes();
    }

    private byte[] getLoginHtml() throws IOException {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/login.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        return fileInputStream.readAllBytes();
    }

    public RequestResult doPost(
            Map<String, String> query,
            HttpCookies cookies,
            Session session
    ) throws IOException {
        try {
            String account = query.get("account");
            String password = query.get("password");
            if (account != null) {
                User user = InMemoryUserRepository.getByAccountAndPassword(account, password);
                session.setAttribute("user", user);
                return new RequestResult(getRedirectHtml(), "text/html;charset=utf-8 ", "HTTP/1.1 302 Found ");
            }

            return new RequestResult(getLoginHtml(), "text/html;charset=utf-8 ");
        } catch (IllegalArgumentException e) {
            return new RequestResult(
                    getAuthorizationFailHtml(),
                    "text/html;charset=utf-8 ",
                    "HTTP/1.1 302 Found "
            );
        }
    }
}
