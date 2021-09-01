package nextstep.jwp.application.controller;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.model.User;
import nextstep.jwp.webserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final String MAPPING_URL = "/register";
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public String mappingUri() {
        return MAPPING_URL;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String body = readStaticFile("register.html");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", getContentType("html") + ";charset=utf-8");
        response.setHeaders(headers);
        response.setStatusCode(StatusCode._200_OK);
        response.setBody(body);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getBody("account");
        String password = request.getBody("password");
        String email = request.getBody("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.setStatusCode(StatusCode._302_FOUND);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", "/index.html");
        response.setHeaders(headers);
    }
}
