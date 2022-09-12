package org.apache.coyote.http11.response;

import static nextstep.jwp.exception.ExceptionType.SERVER_EXCEPTION;
import static org.apache.coyote.http11.common.ContentType.UTF;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.common.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.common.HttpHeaderType.SET_COOKIE;
import static org.apache.coyote.http11.common.StatusCode.FOUND;
import static org.apache.coyote.http11.common.StatusCode.OK;
import static org.apache.coyote.http11.common.Version.HTTP_1_1;

import com.sun.jdi.InternalException;
import java.io.IOException;
import java.util.UUID;
import nextstep.jwp.model.User;
import nextstep.jwp.util.IOUtils;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private HttpStatusLine httpStatusLine;
    private HttpHeaders headers;
    private String responseBody;

    public HttpResponse() {
        this.headers = new HttpHeaders();
    }

    public String generateHttpResponse() {
        return String.join(CRLF, httpStatusLine.generateStatusLine(),
                headers.generateHeadersResponse(),
                responseBody);
    }

    public void setResponseBody(String fileName) throws IOException {
        responseBody = IOUtils.readResourceFile(fileName);
    }

    public void setRedirectUrlResponse(String url) {
        try {
            setResponseBody(url);
            this.httpStatusLine = HttpStatusLine.of(HTTP_1_1, FOUND);
            headers.add(HttpHeader.of(LOCATION, url));
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalException(SERVER_EXCEPTION.getMessage());
        }

    }

    public void setResponseBodyContent(String content) {
        responseBody = content;
    }

    public void setOkHttpStatusLine() {
        this.httpStatusLine = HttpStatusLine.of(HTTP_1_1, OK);
    }

    public void setFoundHttpStatusLine() {
        this.httpStatusLine = HttpStatusLine.of(HTTP_1_1, FOUND);
    }


    public void setCookie(String sessionId) {
        headers.add(HttpHeader.of(SET_COOKIE, HttpCookie.generateCookieValue(sessionId)));
    }

    public void setOKHeader(ContentType fileType) {
        final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, fileType.getValue(), UTF.getValue());
        final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
        headers.add(contentType, contentLength);
    }

    public void setOkResponse(String fileName) {
        try {
            setResponseBody(fileName);
            setOkHttpStatusLine();
            setOKHeader(ContentType.from(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalException(SERVER_EXCEPTION.getMessage());
        }
    }

    public void setSessionAndCookieWithOkResponse(User user, String url) {
        final Session session = setSession(user);
        setCookie(session.getId());
        setRedirectUrlResponse(url);
    }

    public void setFoundResponse(String fileName) {
        try {
            setResponseBody(fileName);
            setFoundHttpStatusLine();
            setOKHeader(ContentType.from(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalException(SERVER_EXCEPTION.getMessage());
        }
    }

    private Session setSession(User succeedLoginUser) {
        final String uuid = UUID.randomUUID().toString();
        final Session session = new Session(uuid);
        session.setAttribute(succeedLoginUser.getAccount(), succeedLoginUser);
        SessionManager.add(session);
        return session;
    }
}
