package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathProcessor {

    private static final Logger log = LoggerFactory.getLogger(PathProcessor.class);

    public static String readContent(String path) throws IOException {
        if (Objects.equals(path, "/")) {
            return helloWorld();
        }

        if (path.startsWith("/login")) {
            return loginContent(path);
        }

        if (Objects.equals(path, "/register")) {
            return registerContent();
        }

        return convertPathToContent(path);
    }

    private static String loginContent(String path) throws IOException {
        int index = path.indexOf("?");
        if (index > 0) {
            String query = path.substring(index + 1);
            String[] queries = query.split("&");
            String account = queries[0].split("=")[1];
            String password = queries[1].split("=")[1];

            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("일치하는 회원을 찾을 수 없습니다."));

            if (user.checkPassword(password)) {
                log.info("로그인 성공한 회원 : {}", user);
            }
        }
        return convertPathToContent("/login.html");
    }

    private static String helloWorld() {
        return "Hello world!";
    }

    private static String registerContent() throws IOException {
        return convertPathToContent("/register.html");
    }

    private static String convertPathToContent(String path) throws IOException {
        URI uri = convertPathToUri(path);
        return Files.readString(Paths.get(uri));
    }

    private static URI convertPathToUri(String path) {
        URL url = PathProcessor.class.getClassLoader().getResource("static" + path);
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
