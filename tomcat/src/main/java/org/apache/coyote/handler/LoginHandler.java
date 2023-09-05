package org.apache.coyote.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Session;
import org.apache.coyote.SessionManager;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.response.ResponseInfo;

import java.util.Optional;
import java.util.UUID;

public class LoginHandler extends RequestHandler {
    private static final SessionManager sessionManager = SessionManager.getInstance();
    public static final String ACCOUNT = "account";

    public LoginHandler(String mappingUri) {
        this.mappingUri = mappingUri;
    }

    @Override
    public ResponseInfo doService(final HttpRequest httpRequest) {
        final String httpMethod = httpRequest.getRequestLine().getHttpMethod();

        if (httpMethod.equals("GET")) {
            return doGet(httpRequest);
        }

        if (httpMethod.equals("POST")) {
            return doPost(httpRequest);
        }

        final String resourcePath = RESOURCE_PATH + PAGE401;
        return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
    }

    @Override
    public ResponseInfo doPost(final HttpRequest httpRequest) {
        final RequestBody body = httpRequest.getRequestBody();

        final String account = body.getByKey(ACCOUNT);
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            String resourcePath = RESOURCE_PATH + PAGE401;
            return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
        }
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user.get());
        sessionManager.add(session);
        String resourcePath = RESOURCE_PATH + INDEX_PAGE;
        return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND, session.getId());
    }

    @Override
    public ResponseInfo doGet(final HttpRequest httpRequest) {
        final RequestHeader header = httpRequest.getRequestHeader();

        if (header.getByKey("Cookie") != null) {
            HttpCookie cookie = HttpCookie.from(header.getByKey("Cookie"));
            String jsessionid = cookie.getByKey("JSESSIONID");

            if (jsessionid != null) {
                if (sessionManager.findSession(jsessionid) == null) {
                    throw new IllegalArgumentException("유효하지 않은 세션입니다.");
                }

                String resourcePath = RESOURCE_PATH + INDEX_PAGE;
                return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND, jsessionid);
            }
        }

        String resourcePath = RESOURCE_PATH + "/login.html";
        return new ResponseInfo(classLoader.getResource(resourcePath), 200, "OK");
    }
}
