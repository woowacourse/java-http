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
            return postLogin(httpRequest, httpResponse);
        }
        return postRegister(httpRequest, httpResponse);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.getUrl().startsWith("/login")) {
            return getLogin(httpRequest, httpResponse);
        }
        return getRegister(httpRequest, httpResponse);
    }

    private HttpResponse postLogin(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.hasJSESSIONID()) {
            final Optional<Session> session = SessionManager.findSession(httpRequest.getJSESSIONID());
            if (session.isEmpty()) {
                return requireAuthByRequestInfo(httpRequest);
            }
            if (session.get().hasAttribute("user")) {
                return redirect(httpRequest, httpResponse);
            }
        }
        return requireAuthByRequestInfo(httpRequest);
    }

    private HttpResponse requireAuthByRequestInfo(final HttpRequest httpRequest)
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
            return assignCookie(httpRequest, user);
        }

        final HttpBody httpBody = HttpBody.createByUrl("/401.html");
        final HttpHeader httpHeader = defaultHeader(StatusCode.MOVED_TEMPORARILY, httpBody, "/401.html");
        httpHeader.location("/401.html");

        return new HttpResponse(httpHeader, httpBody);
    }

    private HttpResponse assignCookie(final HttpRequest httpRequest, final Optional<User> user) throws IOException {
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

    private HttpResponse redirect(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final HttpBody httpBody = HttpBody.createByUrl(REDIRECT_URL);
        final HttpHeader httpHeader = defaultHeader(StatusCode.MOVED_TEMPORARILY, httpBody, httpRequest.getUrl());
        httpHeader.location(REDIRECT_URL);
        return httpResponse.header(httpHeader).body(httpBody);
    }

    private HttpResponse postRegister(final HttpRequest httpRequest, final HttpResponse httpResponse)
            throws IOException {
        final User user = new User(httpRequest.getBodyValue(ACCOUNT), httpRequest.getBodyValue(PASSWORD),
                httpRequest.getBodyValue("email"));
        InMemoryUserRepository.save(user);

        return redirect(httpRequest, httpResponse);
    }

    private HttpResponse getLogin(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.hasJSESSIONID()) {
            final Optional<Session> session = SessionManager.findSession(httpRequest.getJSESSIONID());
            if (session.isPresent() && session.get().hasAttribute("user")) {
                return redirect(httpRequest, httpResponse);
            }
        }
        final HttpBody httpBody = HttpBody.createByUrl(LOGIN_URL);
        final HttpHeader httpHeader = defaultHeader(StatusCode.OK, httpBody, LOGIN_URL);
        return httpResponse.header(httpHeader).body(httpBody);
    }

    private HttpResponse getRegister(final HttpRequest httpRequest, final HttpResponse httpResponse)
            throws IOException {
        final HttpBody httpBody = HttpBody.createByUrl(httpRequest.getUrl());

        final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.OK)
                .contentType(httpRequest.getUrl())
                .contentLength(httpBody.getBody().getBytes().length);

        return httpResponse.header(httpHeader).body(httpBody);
    }
}
