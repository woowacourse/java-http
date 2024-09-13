package org.apache.catalina.controller;

import static org.apache.coyote.http11.message.header.HttpHeaderAcceptType.*;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_TYPE;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.LOCATION;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.SET_COOKIE;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.http11.FileFinder;
import org.apache.coyote.http11.message.CookieParser;
import org.apache.coyote.http11.message.body.HttpBody;
import org.apache.coyote.http11.message.header.HttpHeader;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestLine;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.apache.coyote.http11.message.response.HttpStatusLine;
import org.apache.session.Session;
import org.apache.session.SessionStorage;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    private static final String SESSION_COOKIE_KEY_NAME = "JSESSIONID";
    private static final String NO_CONTENT_LENGTH = "0";
    private static final String ERROR_MESSAGE = "파일을 찾는 과정에서 문제가 발생하였습니다.";

    public LoginController(final String baseUri) {
        super(baseUri);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpHeader requestHeader = request.getHeader();
        final CookieParser cookieParser = new CookieParser();
        final Map<String, String> cookies = requestHeader.parseCookie(cookieParser);

        if (isLogin(cookies)) {
            setAlreadyLoginResponse(response, requestLine);
            return;
        }

        final FileFinder fileFinder = new FileFinder();
        try {
            final Optional<String> fileContent = fileFinder.readFileContent("/login.html");
            fileContent.ifPresentOrElse(
                    content -> setHandleGetSuccessResponse(requestLine, response, content),
                    () -> setServerErrorResponse(requestLine, response)
            );
        } catch (URISyntaxException e) {
            setServerErrorResponse(requestLine, response);
        }
    }

    private boolean isLogin(final Map<String, String> cookies) {
        if (cookies.isEmpty() || !cookies.containsKey(SESSION_COOKIE_KEY_NAME)) {
            return false;
        }

        final String sessionId = cookies.get(SESSION_COOKIE_KEY_NAME);
        final SessionStorage sessionStorage = new SessionStorage();
        return sessionStorage.exist(sessionId);
    }

    private void setAlreadyLoginResponse(final HttpResponse response, final HttpRequestLine requestLine) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.FOUND);
        final HttpHeader responseHeader = new HttpHeader(Map.of(
                LOCATION.getValue(), "http://localhost:8080/index.html",
                CONTENT_TYPE.getValue(), HTML.getValue(),
                CONTENT_LENGTH.getValue(), NO_CONTENT_LENGTH
        ));
        final HttpBody httpBody = new HttpBody("");
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    private void setHandleGetSuccessResponse(
            final HttpRequestLine requestLine,
            final HttpResponse response,
            final String content
    ) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.OK);
        final HttpHeader responseHeader = new HttpHeader(Map.of(
                CONTENT_TYPE.getValue(), HTML.getValue(),
                CONTENT_LENGTH.getValue(), String.valueOf(content.length())
        ));
        final HttpBody httpBody = new HttpBody(content);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    private void setServerErrorResponse(final HttpRequestLine requestLine, final HttpResponse response) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        final HttpHeader responseHeader = new HttpHeader(Map.of(
                LOCATION.getValue(), ERROR_MESSAGE,
                CONTENT_TYPE.getValue(), PLAIN.getValue(),
                CONTENT_LENGTH.getValue(), String.valueOf(ERROR_MESSAGE.length())
        ));
        final HttpBody httpBody = new HttpBody(ERROR_MESSAGE);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpBody body = request.getBody();
        final Map<String, String> formData = body.parseFormDataKeyAndValue();
        final String account = formData.get("account");
        final String password = formData.get("password");
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        final FileFinder fileFinder = new FileFinder();
        if (!validateLoginCredential(user, password)) {
            processNotValidate(requestLine, response, fileFinder);
            return;
        }

        user.ifPresent(u -> {
            final String sessionId = saveSession(u);
            setSuccessLoginResponse(requestLine, response, sessionId);
        });
    }

    private boolean validateLoginCredential(final Optional<User> user, final String password) {
        return user.map(u -> u.checkPassword(password))
                .orElse(false);
    }

    private void processNotValidate(final HttpRequestLine requestLine, final HttpResponse response, final FileFinder fileFinder) {
        try {
            final Optional<String> fileContent = fileFinder.readFileContent("/401.html");
            fileContent.ifPresentOrElse(
                    content -> setNotValidateUserResponse(requestLine, response, content),
                    () -> setServerErrorResponse(requestLine, response)
            );
        } catch (URISyntaxException e) {
            setServerErrorResponse(requestLine, response);
        }
    }

    private void setNotValidateUserResponse(
            final HttpRequestLine requestLine,
            final HttpResponse response,
            final String content
    ) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.UNAUTHORIZED);
        final HttpHeader responseHeader = new HttpHeader(Map.of(
                CONTENT_TYPE.getValue(), HTML.getValue(),
                CONTENT_LENGTH.getValue(), String.valueOf(content.length())
        ));
        final HttpBody httpBody = new HttpBody(content);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    private String saveSession(User user) {
        final String sessionId = UUID.randomUUID().toString();
        final Session session = new Session(sessionId, user);
        final SessionStorage sessionStorage = new SessionStorage();
        sessionStorage.add(session);

        return sessionId;
    }

    private void setSuccessLoginResponse(
            final HttpRequestLine requestLine,
            final HttpResponse response,
            final String sessionId
    ) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.FOUND);
        final HttpHeader responseHeader = new HttpHeader(Map.of(
                SET_COOKIE.getValue(), SESSION_COOKIE_KEY_NAME + "=" + sessionId,
                LOCATION.getValue(), "http://localhost:8080/index.html",
                CONTENT_TYPE.getValue(), HTML.getValue(),
                CONTENT_LENGTH.getValue(), NO_CONTENT_LENGTH
        ));
        final HttpBody httpBody = new HttpBody("");
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }
}
