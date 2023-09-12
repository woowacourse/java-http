package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseBuilder;
import org.apache.coyote.http.SessionManager;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.apache.coyote.http.HttpMethod.GET;
import static org.apache.coyote.http.HttpMethod.POST;

public class LoginController implements Controller {

    private static final String SESSION_ID = "JSESSIONID";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String COOKIE_SEPARATOR = "; ";

    @Override
    public String process(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.getMethod() == GET) {
            return doGet(httpRequest, httpResponse);
        }
        if (httpRequest.getMethod() == POST) {
            return doPost(httpRequest, httpResponse);
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP Method 입니다.");
    }

    private String doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> cookies = httpRequest.findCookies();
        if (cookies.containsKey(SESSION_ID) && SessionManager.isAlreadyLogin(cookies.get(SESSION_ID))) {
            return HttpResponseBuilder.buildStaticFileRedirectResponse(httpRequest, httpResponse, "/index.html");
        }
        return HttpResponseBuilder.buildStaticFileOkResponse(httpRequest, httpResponse, "/login.html");
    }

    private String doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String[] splitRequestBody = httpRequest.getMessageBody().split("&");
        String account = splitRequestBody[0].split(KEY_VALUE_SEPARATOR)[1];
        String password = splitRequestBody[1].split(KEY_VALUE_SEPARATOR)[1];
        try {
            User user = InMemoryUserRepository.findByAccount(account).orElseThrow(UserNotFoundException::new);
            addSession(user, httpRequest, httpResponse);
            return redirect(password, user, httpRequest, httpResponse);
        } catch (UserNotFoundException e) {
            return HttpResponseBuilder.buildStaticFileRedirectResponse(httpRequest, httpResponse, "/401.html");
        }
    }

    private void addSession(User user, HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> cookies = httpRequest.findCookies();
        if (!cookies.containsKey(SESSION_ID)) {
            String uuid = UUID.randomUUID().toString();
            addCookie(httpRequest, httpResponse, uuid);
            cookies.put(SESSION_ID, uuid);
        }
        String jsessionid = cookies.get(SESSION_ID);
        SessionManager.add(jsessionid, user);
    }

    private void addCookie(HttpRequest httpRequest, HttpResponse httpResponse, String uuid) {
        Map<String, String> cookies = httpRequest.findCookies();
        if (cookies.isEmpty()) {
            httpRequest.addHeader(HttpHeader.COOKIE.getName(), SESSION_ID + KEY_VALUE_SEPARATOR + uuid);
            return;
        }
        String existedCookie = httpResponse.joinResponse();
        httpRequest.addHeader(HttpHeader.COOKIE.getName(), existedCookie + COOKIE_SEPARATOR + SESSION_ID + KEY_VALUE_SEPARATOR + uuid);
    }

    private String redirect(String password, User user, HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (user.checkPassword(password)) {
            return HttpResponseBuilder.buildStaticFileRedirectResponse(httpRequest, httpResponse, "/index.html");
        }
        return HttpResponseBuilder.buildStaticFileRedirectResponse(httpRequest, httpResponse, "/401.html");
    }
}
