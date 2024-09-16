package org.apache.catalina.controller;

import static org.reflections.Reflections.log;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionIdGenerator;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpCookies;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_PASSWORD = "password";
    public static final String SUCCESS_LOCATION = "index.html";
    public static final String FAIL_LOCATION = "401.html";
    public static final String SET_COOKIE_HEADER = "Set-Cookie";
    public static final String LOCATION_HEADER = "Location";
    public static final String JSESSION_ID_PREFIX = "JSESSIONID=";

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        final RequestBody body = request.getBody();
        final User user = InMemoryUserRepository.findByAccount(body.getByName(KEY_ACCOUNT))
                .orElseThrow(() -> new IllegalArgumentException("해당 account가 존재하지 않습니다."));
        response.setStatusLine(new StatusLine("HTTP/1.1", "302", "Found"));
        if (user.checkPassword(body.getByName(KEY_PASSWORD))) {
            log.info("user : {}", user);
            final String sessionId = SessionIdGenerator.generateUUID().toString();
            final Session session = new Session(sessionId);
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);
            response.addHeader(SET_COOKIE_HEADER, JSESSION_ID_PREFIX + sessionId);
            response.addLocation(SUCCESS_LOCATION);
            return;
        }
        response.addHeader(LOCATION_HEADER, FAIL_LOCATION);
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        final Headers headers = request.getHeaders();
        final RequestLine requestLine = request.getRequestLine();
        if (requestLine.getPath().equals("/login") && headers.containsField("Cookie")) {
            final HttpCookies httpCookies = new HttpCookies(headers.getByField("Cookie"));
            if (httpCookies.existSessionId("JSESSIONID")) {
                String sessionId = httpCookies.getValue("JSESSIONID");
                if (SessionManager.getInstance().isExistSessionId(sessionId)) {
                    response.setStatusLine(new StatusLine("HTTP/1.1", "302", "Found"));
                    response.addLocation(SUCCESS_LOCATION);
                    return;
                }
            }
        }
        response.setStatusLine(new StatusLine("HTTP/1.1", "200", "OK"));
        response.setBody(ResponseBody.form(request.getRequestLine()));
        response.addContentType();
        response.addContentLength();
    }
}
