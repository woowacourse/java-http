package nextstep.jwp.presentation;

import java.io.IOException;
import java.util.HashMap;
import nextstep.jwp.FileAccess;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.content.ContentType;
import nextstep.jwp.http.response.status.HttpStatus;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String resource = new FileAccess(request.getPath() + ".html").getFile();

        response.setStatusLine(request.getProtocolVersion(), HttpStatus.OK);
        response.addResponseHeader("Content-Type", ContentType.HTML.getType());
        response.addResponseHeader("Content-Length", String.valueOf(resource.getBytes().length));
        response.setResponseBody(resource);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        HashMap<String, String> body = parseBody(request.getBody());

        String account = body.get("account");
        String password = body.get("password");
        String email = body.get("email");

        checkDuplicatedAccount(account);

        InMemoryUserRepository.save(new User(0L, account, password, email));

        response.setStatusLine(request.getProtocolVersion(), HttpStatus.FOUND);
        response.addResponseHeader("Location", "/index.html");
    }

    private void checkDuplicatedAccount(String account) {
        if(InMemoryUserRepository.existAccount(account)) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "중복되는 아이디 입니다.");
        }
    }

    private HashMap<String, String> parseBody(String requestBody) {

        HashMap<String, String> result = new HashMap<>();

        String[] splitBody = requestBody.split("&");

        for (String body : splitBody) {
            String[] keyValue = body.split("=");
            result.put(keyValue[0], keyValue[1]);
        }

        return result;
    }
}
