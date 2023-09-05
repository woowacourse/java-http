package org.apache.coyote;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.RequestBody;
import org.apache.coyote.http11.RequestHeader;
import org.apache.coyote.http11.ResponseInfo;

import java.util.Optional;
import java.util.UUID;

import static org.apache.coyote.http11.Http11Processor.ACCOUNT;
import static org.apache.coyote.http11.Http11Processor.HTTP_FOUND;
import static org.apache.coyote.http11.Http11Processor.INDEX_PAGE;
import static org.apache.coyote.http11.Http11Processor.PAGE401;
import static org.apache.coyote.http11.Http11Processor.RESOURCE_PATH;

public class LoginHandler {
    private final ClassLoader classLoader = getClass().getClassLoader();
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private String mappingUri;

    public LoginHandler(String mappingUri) {
        this.mappingUri = mappingUri;
    }

    public ResponseInfo doPost(final RequestBody body) {
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

    public ResponseInfo doGet(final RequestHeader header) {
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
