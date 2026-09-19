package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.model.QueryParameters;

public class RequestHandler {

    public static User findUser(QueryParameters queryParameters) {
        Map<String, String> values = queryParameters.values();
        checkUserParameter(values);
        String account = values.get("account");
        String password = values.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        validateUser(user, password);
        return user;
    }

    private static void validateUser(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("잘못된 회원정보 입니다.");
        }
    }

    private static void checkUserParameter(Map<String, String> values) {
        if (!values.containsKey("account") || !values.containsKey("password")) {
            throw new IllegalArgumentException("회원정보를 찾지 못했습니다.");
        }
    }
}
