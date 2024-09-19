package org.apache.catalina.controller;

import static org.apache.coyote.http11.message.header.HttpHeaderAcceptType.HTML;
import static org.apache.coyote.http11.message.header.HttpHeaderAcceptType.PLAIN;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_TYPE;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.LOCATION;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.SET_COOKIE;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.message.CookieParser;
import org.apache.coyote.http11.message.body.HttpBody;
import org.apache.coyote.http11.message.header.HttpHeaderField;
import org.apache.coyote.http11.message.header.HttpHeaders;
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
    private static final String END_POINT = "/login";

    @Override
    public boolean canHandle(final HttpRequest request) {
        return matchRequestUriWithBaseUri(request, END_POINT);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpHeaders requestHeader = request.getHeaders();
        final CookieParser cookieParser = new CookieParser();
        final Map<String, String> cookies = requestHeader.parseCookie(cookieParser);

        if (isLogin(cookies)) {
            setAlreadyLoginResponse(response, requestLine);
            return;
        }

        final FileReader fileReader = new FileReader();
        try {
            final String fileContent = fileReader.readFileContent("/login.html");
            setHandleGetSuccessResponse(requestLine, response, fileContent);
        } catch (IllegalStateException e) {
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
        final HttpHeaders responseHeader = new HttpHeaders(List.of(
                new HttpHeaderField(LOCATION.getValue(), "/index.html"),
                new HttpHeaderField(CONTENT_TYPE.getValue(), HTML.getValue()),
                new HttpHeaderField(CONTENT_LENGTH.getValue(), NO_CONTENT_LENGTH)
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
        final HttpHeaders responseHeader = new HttpHeaders(List.of(
                new HttpHeaderField(CONTENT_TYPE.getValue(), HTML.getValue()),
                new HttpHeaderField(CONTENT_LENGTH.getValue(), String.valueOf(content.length()))
        ));
        final HttpBody httpBody = new HttpBody(content);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    private void setServerErrorResponse(final HttpRequestLine requestLine, final HttpResponse response) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        final HttpHeaders responseHeader = new HttpHeaders(List.of(
                new HttpHeaderField(LOCATION.getValue(), ERROR_MESSAGE),
                new HttpHeaderField(CONTENT_TYPE.getValue(), PLAIN.getValue()),
                new HttpHeaderField(CONTENT_LENGTH.getValue(), String.valueOf(ERROR_MESSAGE.length()))
        ));
        final HttpBody httpBody = new HttpBody(ERROR_MESSAGE);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpBody body = request.getBody().get();
        final Map<String, String> formData = body.parseFormDataKeyAndValue();
        final String account = formData.get("account");
        final String password = formData.get("password");
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        final FileReader fileReader = new FileReader();
        if (!validateLoginCredential(user, password)) {
            processNotValidate(requestLine, response, fileReader);
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

    private void processNotValidate(final HttpRequestLine requestLine, final HttpResponse response,
                                    final FileReader fileReader) {
        try {
            final String fileContent = fileReader.readFileContent("/401.html");
            setNotValidateUserResponse(requestLine, response, fileContent);
        } catch (IllegalStateException e) {
            setServerErrorResponse(requestLine, response);
        }
    }

    private void setNotValidateUserResponse(
            final HttpRequestLine requestLine,
            final HttpResponse response,
            final String content
    ) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.UNAUTHORIZED);
        final HttpHeaders responseHeader = new HttpHeaders(List.of(
                new HttpHeaderField(CONTENT_TYPE.getValue(), HTML.getValue()),
                new HttpHeaderField(CONTENT_LENGTH.getValue(), String.valueOf(content.length()))
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
        final HttpHeaders responseHeader = new HttpHeaders(List.of(
                new HttpHeaderField(SET_COOKIE.getValue(), SESSION_COOKIE_KEY_NAME + "=" + sessionId),
                new HttpHeaderField(LOCATION.getValue(), "/index.html"),
                new HttpHeaderField(CONTENT_TYPE.getValue(), HTML.getValue()),
                new HttpHeaderField(CONTENT_LENGTH.getValue(), NO_CONTENT_LENGTH)
        ));
        final HttpBody httpBody = new HttpBody("");
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }
}
