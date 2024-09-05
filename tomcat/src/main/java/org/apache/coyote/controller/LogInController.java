package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.view.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(LogInController.class);

    @Override
    public ModelAndView process(HttpRequest request) {
        String account = request.getQueryStringValue("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
        log.info("user : {}", user);

        return new ModelAndView("login", null);
    }
}
