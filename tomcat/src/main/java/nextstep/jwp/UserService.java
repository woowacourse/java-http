package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.httpmessage.request.requestbody.RequestBodyContent;

public class UserService {
    public static void save(final RequestBodyContent userInput) {
        final User user = new User(userInput.get("account"),
                userInput.get("password"),
                userInput.get("email"));

        if (InMemoryUserRepository.exists(userInput.get("account"))) {
            throw new IllegalArgumentException("이미 존재하는 account입니다. 다른 이름을 입력해주세요.");
        }
        InMemoryUserRepository.save(user);
    }
}
