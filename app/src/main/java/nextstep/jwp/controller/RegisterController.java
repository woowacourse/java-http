package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.model.http.HttpRequest;
import nextstep.jwp.model.http.HttpResponse;
import nextstep.jwp.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class RegisterController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forwardBody(FileUtils.getAllResponseBodies("/register.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        User newUser = new User(request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email"));
        InMemoryUserRepository.save(newUser);

        Optional<User> savedUser = InMemoryUserRepository.findByAccount(request.getParameter("account"));
        LOG.debug("Saved user : {}", savedUser);
        response.redirect("/index.html");
    }
}
