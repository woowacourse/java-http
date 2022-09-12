package nextstep.jwp.controller;

import static org.apache.coyote.http11.message.common.ContentType.HTML;
import static org.apache.coyote.http11.message.common.HttpHeader.COOKIE;
import static org.apache.coyote.http11.message.common.HttpHeader.LOCATION;
import static org.apache.coyote.http11.message.response.HttpStatus.FOUND;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.exception.InvalidRequestException;
import org.apache.coyote.http11.message.common.HttpCookie;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.QueryString;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.util.StaticFileUtil;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse handleGet(final HttpRequest request) {
        boolean jsessionidExists = request.getHttpHeaders().getHeader(COOKIE)
                .map(HttpCookie::parse)
                .map(it -> it.getValues("JSESSIONID"))
                .isPresent();

        if (jsessionidExists) {
            return new HttpResponse.Builder()
                    .contentType(HTML)
                    .body(StaticFileUtil.readFile("/index.html"))
                    .build();
        }

        return new HttpResponse.Builder()
                .contentType(HTML)
                .body(StaticFileUtil.readFile("/login.html"))
                .build();
    }

    @Override
    protected HttpResponse handlePost(final HttpRequest request) {
        QueryString queryString = QueryString.parse(request.getRequestBody());
        Optional<User> user = getUser(queryString);

        if (user.isEmpty()) {
            return redirectTo("/401.html");
        }

        Session session = SessionManager.create();
        session.setAttribute("user", user.get());
        HttpResponse.Builder builder = new HttpResponse.Builder()
                .status(FOUND)
                .header(LOCATION, "/index.html");

        if (!request.getHttpHeaders().hasHeader(COOKIE)) {
            builder.setCookie(HttpCookie.sessionId(session.getId()));
        }

        return builder.build();
    }

    private Optional<User> getUser(final QueryString queryString) {
        String account = queryString.getValues("account").orElseThrow(InvalidRequestException::new);
        String password = queryString.getValues("password").orElseThrow(InvalidRequestException::new);

        return InMemoryUserRepository.findByAccount(account)
                .filter(it -> it.checkPassword(password));
    }
}
