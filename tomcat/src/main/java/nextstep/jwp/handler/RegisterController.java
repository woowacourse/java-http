package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.TargetPath;
import org.apache.coyote.http11.body.FormData;
import org.apache.coyote.http11.controller.FileController;
import org.apache.coyote.http11.controller.GetAndPostController;

public class RegisterController extends GetAndPostController {

    private static final String MAIN_LOCATION = "/index";
    private static final TargetPath SUPPORTED_PATH = new TargetPath("/register").autoComplete();

    private final FileController fileHandler = new FileController();

    @Override
    public boolean supports(HttpRequest httpRequest) {
        if (httpRequest.getMethod().isGet() || httpRequest.getMethod().isPost()) {
            return httpRequest.getTarget().getPath().autoComplete().equals(SUPPORTED_PATH);
        }
        return false;
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        fileHandler.handle(httpRequest, httpResponse);
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        FormData formData = FormData.of(httpRequest.getBody());

        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");

        InMemoryUserRepository.save(new User(account, password, email));
        httpResponse.redirectTo(MAIN_LOCATION);
    }
}
