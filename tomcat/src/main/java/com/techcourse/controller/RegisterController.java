package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Status;

public class RegisterController extends AbstractController {

    private final ResourceController resourceController;

    public RegisterController() {
        this.resourceController = new ResourceController();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse.Builder responseBuilder) {
        resourceController.doGet(request.updatePath("register.html"), responseBuilder);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse.Builder responseBuilder) {
        Map<String, String> body = HttpRequest.extractParameters(request.body());

        if (!body.containsKey("account") ||
                !body.containsKey("password") ||
                !body.containsKey("email")) {
            responseBuilder.status(Status.BAD_REQUEST);
            return;
        }

        String account = body.get("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            responseBuilder.status(Status.CONFLICT);
            return;
        }

        InMemoryUserRepository.save(new User(account, body.get("password"), body.get("email")));
        responseBuilder.status(Status.FOUND)
                .location("/index.html");
    }
}
