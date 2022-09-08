package nextstep.jwp.presentation;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParam;
import org.apache.coyote.http11.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private static final String REDIRECT_URL = "/index.html";
    private static final String LOGIN_URL = "/login.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";


    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.getUrl().startsWith("/login")) {
            return login(httpRequest, httpResponse);
        }
        return register(httpRequest, httpResponse);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.getUrl().startsWith("/login")) {
            return login(httpRequest, httpResponse);
        }
        return register(httpRequest, httpResponse);
    }

    private HttpResponse login(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.hasJSESSIONID()) {
            final Session session = SessionManager.findSession(httpRequest.getJSESSIONID());
            if (session == null && httpRequest.getMethod().equals("POST")) {
                return requireAuthByRequestInfo(httpRequest, httpResponse);
            }
            if (session == null && httpRequest.getMethod().equals("GET")) {
                final HttpBody httpBody = HttpBody.createByUrl(LOGIN_URL);

                final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.OK)
                        .contentType(LOGIN_URL)
                        .contentLength(httpBody.getBody().getBytes().length);

                return new HttpResponse(httpHeader, httpBody);
            }
            if (session.hasAttribute("user")) {
                final HttpBody httpBody = HttpBody.createByUrl(REDIRECT_URL);

                final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.MOVED_TEMPORARILY)
                        .contentType(httpRequest.getUrl())
                        .contentLength(httpBody.getBody().getBytes().length)
                        .location(REDIRECT_URL);

                return new HttpResponse(httpHeader, httpBody);
            }
        }

        if (httpRequest.getMethod().equals("GET")) {
            final HttpBody httpBody = HttpBody.createByUrl(LOGIN_URL);

            final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.OK)
                    .contentType(LOGIN_URL)
                    .contentLength(httpBody.getBody().getBytes().length);

            return new HttpResponse(httpHeader, httpBody);
        }
        return requireAuthByRequestInfo(httpRequest, httpResponse);
    }

    private HttpResponse requireAuthByRequestInfo(final HttpRequest httpRequest, final HttpResponse httpResponse)
            throws IOException {
        if (QueryParam.isQueryParam(httpRequest.getUrl())) {
            final QueryParam queryParam = new QueryParam(httpRequest.getUrl());
            if (queryParam.matchParameters(ACCOUNT) && queryParam.matchParameters(PASSWORD)) {
                return authentication(httpRequest, queryParam.getValue(ACCOUNT), queryParam.getValue(PASSWORD));
            }
        }

        final String account = httpRequest.getBodyValue(ACCOUNT);
        final String password = httpRequest.getBodyValue(PASSWORD);
        return authentication(httpRequest, account, password);
    }

    private HttpResponse authentication(final HttpRequest httpRequest, final String account, final String password)
            throws IOException {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            LOGGER.info(user.get().toString());

            final Session session = SessionManager.add(HttpCookie.makeJSESSIONID());
            session.addAttribute("user", user.get());

            final HttpBody httpBody = HttpBody.createByUrl(REDIRECT_URL);

            final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.MOVED_TEMPORARILY)
                    .cookie(session.getId())
                    .contentType(httpRequest.getUrl())
                    .contentLength(httpBody.getBody().getBytes().length)
                    .location(REDIRECT_URL);

            return new HttpResponse(httpHeader, httpBody);
        }

        final HttpBody httpBody = HttpBody.createByUrl("/401.html");

        final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.MOVED_TEMPORARILY)
                .contentType("/401.html")
                .contentLength(httpBody.getBody().getBytes().length)
                .location("/401.html");

        return new HttpResponse(httpHeader, httpBody);
    }

    private HttpResponse register(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.getUrl().startsWith("/register") && httpRequest.getMethod().equals("POST")) {
            final String account = httpRequest.getBodyValue(ACCOUNT);
            final String password = httpRequest.getBodyValue(PASSWORD);
            final String email = httpRequest.getBodyValue("email");

            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            final HttpBody httpBody = HttpBody.createByUrl(REDIRECT_URL);

            final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.MOVED_TEMPORARILY)
                    .contentType(httpRequest.getUrl())
                    .contentLength(httpBody.getBody().getBytes().length)
                    .location(REDIRECT_URL);

            return new HttpResponse(httpHeader, httpBody);
        }

        final HttpBody httpBody = HttpBody.createByUrl(httpRequest.getUrl());

        final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.OK)
                .contentType(httpRequest.getUrl())
                .contentLength(httpBody.getBody().getBytes().length);

        return new HttpResponse(httpHeader, httpBody);
    }
}
