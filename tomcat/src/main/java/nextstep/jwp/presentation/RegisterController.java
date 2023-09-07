package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.FileIOReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String INDEX = "/index.html";
    public static final RegisterController instance = new RegisterController();

    private RegisterController() {
    }

    public static Controller getInstance() {
        return instance;
    }

    @Override
    public HttpResponse doGet(HttpRequest request, HttpResponse response) {
        String responseBody = FileIOReader.readFile(request.getRequestUrl());
        return response.contentType(request.getAccept())
                       .body(responseBody);
    }

    @Override
    public HttpResponse doPost(HttpRequest request, HttpResponse response) {
        InMemoryUserRepository.save(new User(
                request.getBodyValue("account"),
                request.getBodyValue("password"),
                request.getBodyValue("email")
        ));
        return response.contentType(request.getAccept())
                       .redirect(INDEX);
    }
}
