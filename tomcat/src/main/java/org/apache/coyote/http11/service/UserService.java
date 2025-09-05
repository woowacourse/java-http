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

    public ContentParseResult doRequest(Map<String, String> query) throws IOException {
        String account = query.get("account");
        String password = query.get("password");
        if (account != null) {
            User user = InMemoryUserRepository.findByAccountAndPassword(account, password);

            log.info(user.toString());
        }

        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/login.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        return new ContentParseResult(fileInputStream.readAllBytes(), "text/html;charset=utf-8 ");
    }
}
