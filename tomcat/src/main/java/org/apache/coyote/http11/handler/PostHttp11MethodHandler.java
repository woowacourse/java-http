package org.apache.coyote.http11.handler;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;
import static nextstep.jwp.db.InMemoryUserRepository.save;
import static org.apache.coyote.header.HttpMethod.POST;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.model.User;
import org.apache.coyote.header.HttpMethod;
import org.apache.coyote.util.RequestExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostHttp11MethodHandler implements Http11MethodHandler {

    private static final Logger log = LoggerFactory.getLogger(PostHttp11MethodHandler.class);

    @Override
    public HttpMethod supportMethod() {
        return POST;
    }

    @Override
    public String handle(final String headers, final String payload) {
        String targetPath = RequestExtractor.extractTargetPath(headers);
        Map<String, String> requestBody = toMap(payload);

        if (targetPath.contains("login")) {
            return login(requestBody);
        }
        if (targetPath.contains("register")) {
            return register(requestBody);
        }
        return String.join("\r\n",
                "HTTP/1.1 404 Not Found");
    }

    private Map<String, String> toMap(final String payload) {
        Map<String, String> result = new HashMap<>();
        String[] params = payload.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    private String register(Map<String, String> requestBody) {
        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        save(user);

        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + "index.html");
    }

    private String login(Map<String, String> requestBody) {
        User user = findByAccount(requestBody.get("account"))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        if (user.checkPassword(requestBody.get("password"))) {
            log.info("로그인 성공! -> {}", user);

            return String.join("\r\n",
                    "HTTP/1.1 302 Found",
                    "Location: " + "index.html",
                    "Set-Cookie: " + "JSESSIONID=" + UUID.randomUUID());
        }
        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + "401.html");
    }
}
