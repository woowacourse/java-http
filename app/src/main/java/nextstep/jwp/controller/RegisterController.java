package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.RequestParams;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String content = FileReader.file("/register.html");

        response.writeStatusLine(HttpStatus.OK);
        response.writeHeaders(content, ContentType.HTML);
        response.writeBody(content);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final String[] body = request.getBody().split("&");
        final Map<String, String> registerInfo = RequestParams.requestParams(body);

        final String account = registerInfo.get("account");
        final String password = registerInfo.get("password");
        final String email = registerInfo.get("email");

        final String content = FileReader.file(request.getUri());

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            response.writeStatusLine(HttpStatus.OK);
            response.writeHeaders(content, ContentType.HTML);
            response.writeBody(content);
            return;
        }

        final User user = new User(InMemoryUserRepository.findCurrentId(), account, password,
                email);
        InMemoryUserRepository.save(user);

        response.writeStatusLine(HttpStatus.FOUND);
        response.writeRedirect("/index.html");
    }
}
