package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.web.ContentType;
import nextstep.jwp.web.HttpRequest;
import nextstep.jwp.web.HttpResponse;
import nextstep.jwp.web.HttpStatus;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class RegisterController extends AbstractController {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        String resourcePath = resource.getPath();

        response.status(HttpStatus.OK)
                .contentType(ContentType.toHttpNotationFromFileExtension(resource.getFile()))
                .body(new String(Files.readAllBytes(Path.of(resourcePath))));
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String requestBody = request.getRequestBody();

        Map<String, String> parameters = Arrays.stream(requestBody.split("&"))
                .map(row -> row.split("="))
                .collect(toMap(element -> element[0], element -> element[1]));

        User user = new User(parameters.get("account"), parameters.get("password"), parameters.get("email"));
        User save = InMemoryUserRepository.save(user);

        response.status(HttpStatus.FOUND)
                .location("/index.html");
    }
}
