package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.view.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(LogInController.class);

    @Override
    public ModelAndView process(HttpRequest request) {
        String account = request.getQueryStringValue("account");
        String password = request.getQueryStringValue("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccountAndPassword(account, password);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.info("optionalUser : {}", user);
            return new ModelAndView("/login", user.toMap());
        }

        return new ModelAndView("/401.html", null);
    }
}
