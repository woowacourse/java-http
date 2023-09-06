package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.FileFinder;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> body = Arrays.stream(request.getBody().split("&"))
            .map(it -> it.split("="))
            .collect(Collectors.toMap(
                keyAndValue -> keyAndValue[0],
                keyAndValue -> keyAndValue[1]));
        InMemoryUserRepository.save(new User(
            body.get("account"),
            body.get("password"),
            body.get("email")
        ));
        response.toRedirect("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setHttpStatus(HttpStatus.OK);
        response.setBody(FileFinder.getFileContent("/register.html"));
    }
}
