package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.http11.model.FormParameters;
import org.apache.coyote.http11.model.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static byte[] get(String url) throws URISyntaxException, IOException {
        if ("/".equals(url)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }
        if ("/login".equals(url) || "/register".equals(url)) {
            url += ".html";
        }
        String resourcePath = "static" + url;
        URL resource = RequestHandler.class.getClassLoader().getResource(resourcePath);
        Path path = Path.of(resource.toURI());
        return Files.readAllBytes(path);
    }

    public static String post(UriInfo uriInfo, FormParameters formParameters) {
        if ("/login".equals(uriInfo.path()) && formParameters.hasQueryParameters()) {
            try {
                User user = findUser(formParameters.values());
                log.info("로그인 사용자: {}", user.getAccount());
                return "/index.html";
            } catch (IllegalArgumentException e) {
                return "/401.html";
            }
        }
        if ("/register".equals(uriInfo.path()) && formParameters.hasQueryParameters()) {
            saveUser(formParameters.values());
            return "/index.html";
        }
        return "/401.html";
    }

    private static void saveUser(Map<String, String> values) {
        String email = values.get("email");
        String account = values.get("account");
        String password = values.get("password");
        InMemoryUserRepository.save(new User(account, password, email));
    }

    public static User findUser(Map<String, String> values) {
        checkUserParameter(values);
        String account = values.get("account");
        String password = values.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        validateUser(user, password);
        return user;
    }

    private static void validateUser(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("잘못된 회원정보 입니다.");
        }
    }

    private static void checkUserParameter(Map<String, String> values) {
        if (!values.containsKey("account") || !values.containsKey("password")) {
            throw new IllegalArgumentException("회원정보를 찾지 못했습니다.");
        }
    }
}
