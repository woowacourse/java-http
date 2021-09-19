package nextstep.jwp.controller;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.utils.RequestParams;
import nextstep.jwp.utils.Resources;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String content = FileReader.file(Resources.REGISTER.getResource());

        response.setHttpStatus(HttpStatus.OK);

        response.addHeaders("Content-Type", ContentType.HTML.getType());
        response.addHeaders("Content-Length", String.valueOf(content.getBytes().length));

        response.setBody(content);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final String[] body = request.getBody().split("&");
        final Map<String, String> registerInfo = RequestParams.requestParams(body);

        final String account = registerInfo.get("account");
        final String password = registerInfo.get("password");
        final String email = registerInfo.get("email");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            final String content = FileReader.file(request.getUri());

            response.setHttpStatus(HttpStatus.BAD_REQUEST);

            response.addHeaders("Content-Type", ContentType.HTML.getType());
            response.addHeaders("Content-Length", String.valueOf(content.getBytes().length));

            response.setBody(content);
            return;
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        if (request.getCookie().getCookies("JSESSIONID") == null) {
            response.addHeaders("Set-Cookie",
                    String.format("JSESSIONID=%s", UUID.randomUUID()));
        }

        response.setHttpStatus(HttpStatus.FOUND);
        response.setBody(Resources.INDEX.getResource());
    }
}
