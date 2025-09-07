package org.apache.coyote.http11.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.parser.ContentParseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class UserService implements HttpService {

    private static final Logger log = LoggerFactory.getLogger(HttpService.class);

    public ContentParseResult doGet(Map<String, String> query) throws IOException {
        try {
            String account = query.get("account");
            String password = query.get("password");
            if (account != null) {
                User user = InMemoryUserRepository.getByAccountAndPassword(account, password);
                log.info(user.toString());
                return new ContentParseResult(getRedirectHtml(), "text/html;charset=utf-8 ", "HTTP/1.1 302 Found ");
            }

            return new ContentParseResult(getLoginHtml(), "text/html;charset=utf-8 ");
        } catch (IllegalArgumentException e) {
            return new ContentParseResult(
                    getAuthorizationFailHtml(),
                    "text/html;charset=utf-8 ",
                    "HTTP/1.1 302 Found "
            );
        }
    }

    private static byte[] getAuthorizationFailHtml() throws IOException {
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

    private static byte[] getLoginHtml() throws IOException {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/login.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        return fileInputStream.readAllBytes();
    }

    public ContentParseResult doPost(Map<String, String> query) throws IOException {
        String account = query.get("account");
        String password = query.get("password");
        User user = InMemoryUserRepository.getByAccountAndPassword(account, password);
        log.info(user.toString());

        return new ContentParseResult(getLoginHtml(), "text/html;charset=utf-8 ");
    }
}
