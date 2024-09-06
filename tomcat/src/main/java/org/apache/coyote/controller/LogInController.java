package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(LogInController.class);

    @Override
    public HttpResponse process(HttpRequest request) {
        String account = request.body().getAttribute("account");
        String password = request.body().getAttribute("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccountAndPassword(account, password);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.info("optionalUser : {}", user);
            return new HttpResponse(
                    new HttpResponseStatusLine(302, "Found"),
                    Map.of("Location", "/index.html"),
                    null
            );
        }

        return new HttpResponse(
                new HttpResponseStatusLine(302, "Found"),
                Map.of("Location", "/401.html"),
                null
        );
    }
}
