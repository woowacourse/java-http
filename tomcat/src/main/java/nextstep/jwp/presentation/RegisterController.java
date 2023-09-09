package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final RegisterController INSTANCE = new RegisterController();

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = FileReader.read(httpRequest.getUri());
        return httpResponse
                .addBaseHeader(httpRequest.getContentType())
                .addBody(body);
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        join(httpRequest);
        return httpResponse
                .addBaseHeader(httpRequest.getContentType())
                .redirect("/index.html");
    }

    private static void join(HttpRequest httpRequest) {
        InMemoryUserRepository.save(new User(
                httpRequest.getBodyValue("account"),
                httpRequest.getBodyValue("password"),
                httpRequest.getBodyValue("email")
        ));
    }
}
