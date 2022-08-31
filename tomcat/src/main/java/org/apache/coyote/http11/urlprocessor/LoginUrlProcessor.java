package org.apache.coyote.http11.urlprocessor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UrlResponse;

public class LoginUrlProcessor extends DefaultUrlProcessor {
    private static final Pattern pattern = Pattern.compile(".+\\?account=(.*)&password=(.*)");

    @Override
    public UrlResponse getResponse(String url) throws IOException {
        Matcher matcher = pattern.matcher(url);
        validateUrlPattern(matcher);

        final String account = matcher.group(1);
        final String password = matcher.group(2);


        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 account 입니다."));

        validatePassword(user, password);

        final String responseBody = getResponseBody("static/login.html");
        final String contentType = ContentType.HTML.getValue();
        System.out.println(user);

        return new UrlResponse(responseBody, contentType);
    }

    private void validateUrlPattern(Matcher matcher) {
        if (!matcher.matches()) {
            throw new IllegalArgumentException("잘못된 로그인 요청입니다.");
        }
    }

    private void validatePassword(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("잘못된 password 입니다.");
        }
    }
}


