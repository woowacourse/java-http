package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthHandler.class);

    public static String authenticate(Map<String, String> authInfo, HttpCookie httpCookie) {
        User user = InMemoryUserRepository.findByAccount(authInfo.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 정보입니다."));
        if (!user.checkPassword(authInfo.get("password"))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        String sessionId = getOrCreateSessionId(httpCookie);
        saveUserInSession(sessionId, user);
        log.info("로그인 성공: {}", user);
        return sessionId;
    }

    public static String register(Map<String, String> registerInfo, HttpCookie httpCookie) {
        User user = new User(registerInfo.get("account"), registerInfo.get("password"), registerInfo.get("email"));
        InMemoryUserRepository.save(user);
        
        String sessionId = getOrCreateSessionId(httpCookie);
        saveUserInSession(sessionId, user);
        log.info("회원가입 성공: {}", user);
        return sessionId;
    }

    private static String getOrCreateSessionId(HttpCookie httpCookie) {
        if (httpCookie.hasJSESSIONID()) {
            log.info("기존 세션 사용: {}", httpCookie.getJSESSIONID());
            return httpCookie.getJSESSIONID();
        }
        
        Session session = new Session();
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        log.info("새 세션 생성: {}", session.getId());
        return session.getId();
    }

    private static void saveUserInSession(String sessionId, User user) {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(sessionId);
        if (session != null) {
            session.setAttribute("user", user);
        }
    }
}
