package nextstep.jwp.presentation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.model.HttpCookie;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpVersion;
import org.apache.coyote.support.Session;
import org.apache.coyote.support.SessionManager;
import org.apache.coyote.util.Cookies;
import org.apache.coyote.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public HttpResponse doGet(final HttpRequest httpRequest) {
        HttpHeaders headers = httpRequest.getHeaders();
        String cookie = headers.getValue("Cookie");
        if (cookie != null) {
            String[] split = cookie.split(";");
            for (String s : split) {
                String[] split1 = s.split("=");
                String session = split1[0];
                if (session.equals("JSESSIONID")) {
                    String sessionId = split1[1];
                    Session foundSession = SessionManager.findSession(sessionId);
                    if (foundSession != null) {
                        return response("Location: /index.html");
                    }
                }
            }
        }

        String body = FileUtils.readAllBytes(httpRequest.getUri().getValue());
        return HttpResponse.builder()
                .body(body)
                .version(httpRequest.getVersion())
                .status(HttpStatus.OK.getValue())
                .headers("Content-Type: " + httpRequest.getUri().getContentType().getValue(),
                        "Content-Length: " + body.getBytes().length)
                .build();
    }

    public HttpResponse doPost(final HttpRequest httpRequest) {
        Map<String, String> values = getAccountAndPassword(httpRequest);
        String account = values.get("account");
        String password = values.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("not found account"));

        if (!user.checkPassword(password)) {
            return response("Location: /401.html");
        }

        log.info(user.toString());
        String cookie = Cookies.ofJSessionId();
        Session session = new Session(cookie);
        session.setAttribute("user", user);
        SessionManager.add(session);
        return HttpResponse.builder()
                .version(HttpVersion.HTTP_1_1)
                .status(HttpStatus.FOUND.getValue())
                .headers("Location: /index.html", "Set-Cookie: " + HttpCookie.JSESSIONID + "=" + cookie)
                .build();
    }

    private Map<String, String> getAccountAndPassword(final HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        String[] split = body.split("&");
        Map<String, String> values = new HashMap<>();
        for (String value : split) {
            String[] keyAndValue = value.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        return values;
    }

    public HttpResponse register(final HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        String[] split = body.split("&");
        Map<String, String> values = new HashMap<>();
        for (String value : split) {
            String[] keyAndValue = value.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        String account = values.get("account");
        Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
        if (byAccount.isPresent()) {
            return response("Location: /404.html");
        }
        User user = new User(account, values.get("password"), values.get("email"));

        InMemoryUserRepository.save(user);
        return response("Location: /index.html");
    }

    private HttpResponse response(final String location) {
        return HttpResponse.builder()
                .version(HttpVersion.HTTP_1_1)
                .status(HttpStatus.FOUND.getValue())
                .headers(location)
                .build();
    }
}
