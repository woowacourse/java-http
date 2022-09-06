package nextstep.jwp.view;

import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.common.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserOutput {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void outputUserInformation(final Request request) {
        final String userAccount = request.getQueryStringValue("account")
                .orElse("");
        InMemoryUserRepository.findByAccount(userAccount)
                .ifPresent(user -> log.info(user.toString()));
    }
}
