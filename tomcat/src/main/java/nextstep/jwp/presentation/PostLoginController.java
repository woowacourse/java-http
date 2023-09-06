package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;
import org.apache.coyote.http11.SessionManager;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class PostLoginController implements Controller {

    private static final String SESSION_ID = "JSESSIONID";
    private static final String KEY_VALUE_SEPARATOR = "=";

    @Override
    public String process(HttpRequestParser httpRequestParser, HttpResponseBuilder httpResponseBuilder) throws IOException {
        String[] splitRequestBody = httpRequestParser.getMessageBody().split("&");
        String account = splitRequestBody[0].split(KEY_VALUE_SEPARATOR)[1];
        String password = splitRequestBody[1].split(KEY_VALUE_SEPARATOR)[1];
        try {
            User user = InMemoryUserRepository.findByAccount(account).orElseThrow(UserNotFoundException::new);
            addSession(user, httpRequestParser);
            return redirect(password, user, httpRequestParser, httpResponseBuilder);
        } catch (UserNotFoundException e) {
            return httpResponseBuilder.buildStaticFileRedirectResponse(httpRequestParser, "/401.html");
        }
    }

    private void addSession(User user, HttpRequestParser httpRequestParser) {
        Map<String, String> cookies = httpRequestParser.findCookies();
        if (!cookies.containsKey(SESSION_ID)) {
            String uuid = UUID.randomUUID().toString();
            addCookie(httpRequestParser, cookies, uuid);
            cookies.put(SESSION_ID, uuid);
        }
        String jsessionid = cookies.get(SESSION_ID);
        SessionManager.add(jsessionid, user);
    }

    private void addCookie(HttpRequestParser httpRequestParser, Map<String, String> cookies, String uuid) {
        if (cookies.isEmpty()) {
            httpRequestParser.addHeader("Cookie", SESSION_ID + "=" + uuid);
            return;
        }
        String existedCookie = joinExistedCookie(cookies);
        httpRequestParser.addHeader("Cookie", existedCookie + "; " + SESSION_ID + "=" + uuid);
    }

    private String joinExistedCookie(Map<String, String> cookies) {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((cookie1, cookie2) -> cookie1 + "; " + cookie2)
                .orElse("");
    }

    private String redirect(String password, User user, HttpRequestParser httpRequestParser, HttpResponseBuilder httpResponseBuilder) throws IOException {
        if (user.checkPassword(password)) {
            return httpResponseBuilder.buildStaticFileRedirectResponse(httpRequestParser, "/index.html");
        }
        return httpResponseBuilder.buildStaticFileRedirectResponse(httpRequestParser, "/401.html");
    }
}
