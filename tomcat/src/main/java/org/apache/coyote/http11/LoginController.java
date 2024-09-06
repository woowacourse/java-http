package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public String process(String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        String queryString = uri.substring(index + 1);
        String[] input = queryString.split("&");
        String[] accountInfo = input[0].split("=");
        String account = accountInfo[1];

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("입력된 정보와 일치하는 유저를 찾을 수 없습니다."));
        log.info("user : {}", user);

        return uri.substring(0, index) + ".html";
    }
}
