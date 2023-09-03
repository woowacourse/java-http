package nextstep.jwp.controller;

import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class LoginController implements RestController {

    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String HOME_PAGE = "/index.html";

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getPath().equals("/login") && request.hasQueryStrings();
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        HttpHeaders headers = HttpHeaders.RestHeaders();
        try {
            final User user = InMemoryUserRepository.findByAccount(request.getQueryString("account"))
                                                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 계정입니다."));

            if (user.checkPassword(request.getQueryString("password"))) {
                headers.put(HttpHeaders.LOCATION, HOME_PAGE);
                return new HttpResponse(HttpStatusCode.FOUND, headers, "");
            }

            headers.put(HttpHeaders.LOCATION, UNAUTHORIZED_PAGE);
            return new HttpResponse(HttpStatusCode.FOUND, headers, "");
        } catch (NoSuchElementException e) {

            headers.put(HttpHeaders.LOCATION, UNAUTHORIZED_PAGE);
            return new HttpResponse(HttpStatusCode.FOUND, headers, "");
        }
    }
}
