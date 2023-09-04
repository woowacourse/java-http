package org.apache.coyote.handler.mapping;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class LoginPageMapping extends LoginFilter implements HandlerMapping {

    private static final Logger log = LoggerFactory.getLogger(LoginPageMapping.class);

    @Override
    public boolean supports(final String httpMethod, final String requestUri) {
        return "GET".equals(httpMethod) &&
                requestUri.contains("login");
    }

    @Override
    public String handle(final String requestUri, final Map<String, String> headers, final String requestBody) throws IOException {
        if (headers.containsKey("Cookie")) {
            final HttpCookie cookies = HttpCookie.from(headers.get("Cookie"));
            if (isAlreadyLogined(cookies.get("JSESSIONID"))) {
                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /index.html ");
            }
        }

        final String[] parsedRequestUri = requestUri.split("\\?");
        if (requestUri.contains("?")) {
            final String uri = parsedRequestUri[0];
            final Map<String, String> queryStrings = Arrays.stream(parsedRequestUri[1].split("&"))
                    .map(param -> param.split("="))
                    .collect(Collectors.toMap(param -> param[0], param -> param[1]));

            final String account = queryStrings.get("account");
            final String password = queryStrings.get("password");

            try {
                final User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("잘못된 계정입니다. 다시 입력해주세요."));

                if (!user.checkPassword(password)) {
                    throw new IllegalArgumentException("잘못된 비밀번호입니다. 다시 입력해주세요.");
                }
                log.info("로그인 성공! user = {}", user);
            } catch (final IllegalArgumentException e) {
                log.warn("login error = {}", e);
                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /401.html ");
            }

            return String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /index.html ");
        }

        final String filePath = "static/login.html";
        final URL fileUrl = getClass().getClassLoader().getResource(filePath);
        final Path path = new File(fileUrl.getPath()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
