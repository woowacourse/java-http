package org.apache.catalina.controller;

import static org.apache.coyote.http11.message.header.HttpHeaderAcceptType.HTML;
import static org.apache.coyote.http11.message.header.HttpHeaderAcceptType.JSON;
import static org.apache.coyote.http11.message.header.HttpHeaderAcceptType.PLAIN;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_TYPE;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.LOCATION;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.http11.FileReader;
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

public class RegisterController extends AbstractController {

    private static final String ERROR_MESSAGE = "파일을 찾는 과정에서 문제가 발생하였습니다.";

    private final String endPoint = "/register";

    @Override
    public boolean canHandle(final HttpRequest request) {
        return matchRequestUriWithBaseUri(request, endPoint);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpRequestLine requestLine = request.getRequestLine();

        final FileReader fileReader = new FileReader();
        try {
            final String fileContent = fileReader.readFileContent("/register.html");
            setHandleGetSuccessResponse(requestLine, response, fileContent);
        } catch (IllegalStateException e) {
            setServerErrorResponse(requestLine, response);
        }
    }

    private void setHandleGetSuccessResponse(
            final HttpRequestLine requestLine,
            final HttpResponse response,
            final String content
    ) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.OK);
        final HttpHeaders responseHeader = new HttpHeaders(List.of(
                new HttpHeaderField(CONTENT_TYPE.getValue(), HTML.getValue()),
                new HttpHeaderField(CONTENT_LENGTH.getValue(), String.valueOf(content.length())
                )));
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
                new HttpHeaderField(CONTENT_LENGTH.getValue(), String.valueOf(ERROR_MESSAGE.length())
                )));
        final HttpBody httpBody = new HttpBody(ERROR_MESSAGE);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final Map<String, String> formData = request.getBody().get().parseFormDataKeyAndValue();
        final String account = formData.get("account");
        final String password = formData.get("password");
        final String email = formData.get("email");

        final Optional<User> user = register(account, password, email);
        if (user.isEmpty()) {
            setDuplicateUserAccountErrorResponse(requestLine, response);
            return;
        }

        setHandlePostSuccessResponse(requestLine, response);
    }

    private Optional<User> register(
            final String account,
            final String password,
            final String email
    ) {
        if (!checkDuplicateAccount(account)) {
            return Optional.empty();
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        final SessionStorage sessionStorage = new SessionStorage();
        final String sessionId = generateSessionId();
        final Session session = new Session(sessionId, user);
        sessionStorage.add(session);

        return Optional.of(user);
    }

    private boolean checkDuplicateAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account).isEmpty();
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    private void setDuplicateUserAccountErrorResponse(final HttpRequestLine requestLine, final HttpResponse response) {
        final String responseBody = "{\"message\": \"이미 존재하는 사용자 계정입니다.\"}";
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.BAD_REQUEST);
        final HttpHeaders responseHeader = new HttpHeaders(List.of(
                new HttpHeaderField(CONTENT_TYPE.getValue(), JSON.getValue()),
                new HttpHeaderField(CONTENT_LENGTH.getValue(), String.valueOf(responseBody.length()))
        ));
        final HttpBody httpBody = new HttpBody(responseBody);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    private void setHandlePostSuccessResponse(final HttpRequestLine requestLine, final HttpResponse response) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.FOUND);
        final HttpHeaders responseHeader = new HttpHeaders(List.of(
                new HttpHeaderField(LOCATION.getValue(), "/index.html"),
                new HttpHeaderField(CONTENT_TYPE.getValue(), HTML.getValue()),
                new HttpHeaderField( CONTENT_LENGTH.getValue(), String.valueOf(0))
        ));
        final HttpBody httpBody = new HttpBody("");
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }
}
