package org.apache.catalina;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.Session;
import java.util.Optional;
import java.util.UUID;

public class AuthManager {

    public static Session authenticate(String requestBody) {
        if (!isUserExist(requestBody)) {
            throw new IllegalArgumentException("회원가입된 유저가 아닙니다.");
        }

        String uuid = UUID.randomUUID().toString();
        Session session = new Session(uuid);
        session.setAttribute("user", session);
        SessionManager.add(session);

        return session;
    }

    private static boolean isUserExist(String requestBody) {
        String account = requestBody.split("&")[0].split("=")[1];
        String password = requestBody.split("&")[1].split("=")[1];

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        return optionalUser.isPresent() && optionalUser.get().checkPassword(password);
    }
}
