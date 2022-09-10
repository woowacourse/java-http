package nextstep.jwp.ui;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PAGE_PATH = "register.html";
    private static final String REDIRECT_INDEX_PAGE_PATH = "redirect:index.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.initResponseValues(request, ResponseEntity.body(REGISTER_PAGE_PATH));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody requestBody = request.getRequestBody();
        saveUser(requestBody);
        ResponseEntity responseEntity = ResponseEntity.body(REDIRECT_INDEX_PAGE_PATH).status(HttpStatus.REDIRECT);
        response.initResponseValues(request, responseEntity);
    }

    private static void saveUser(RequestBody requestBody) {
        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
