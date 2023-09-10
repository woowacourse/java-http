package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.HttpRequestBody;
import org.apache.coyote.request.RequestUri;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.utils.FileUtils;

import static org.apache.coyote.common.ContentType.HTML;
import static org.apache.coyote.response.HttpStatus.FOUND;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        HttpRequestBody httpRequestBody = request.getRequestBody();

        String account = httpRequestBody.getValue("account");
        String password = httpRequestBody.getValue("password");
        String email = httpRequestBody.getValue("email");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException("중복 ID 입니다");
        }

        InMemoryUserRepository.save(new User(account, password, email));

        response = new HttpResponse.Builder()
                .status(FOUND)
                .contentType(HTML)
                .header("Location", "/index.html")
                .build();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        RequestUri requestUri = request.getRequestLine().getRequestUri();

        response = new HttpResponse.Builder()
                .contentType(ContentType.from(requestUri.getExtension()))
                .body(FileUtils.readFile("/register.html"))
                .build();
    }
}
