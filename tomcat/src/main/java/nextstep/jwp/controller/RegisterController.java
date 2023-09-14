package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpHeader;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StatusLine;

public class RegisterController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getRequestUri().equals("register");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.OK));
        response.addHeader(HttpHeader.CONTENT_TYPE.getName(), ContentType.HTML.getType());
        final String content = readResponseBody(request.getRequestUri() + ContentType.HTML.getExtension());
        response.setResponseBody(content);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        final Map<String, String> registerInfo = Arrays.stream(request.getRequestBody().split("&"))
                .map(input -> input.split("="))
                .collect(Collectors.toMap(info -> info[0], info -> info[1]));

        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.FOUND));
        if (InMemoryUserRepository.findByAccount(registerInfo.get("account")).isPresent()) {
            response.addHeader(HttpHeader.LOCATION.getName(), "register.html");
            return;
        }

        final User newUser = new User(registerInfo.get("account"), registerInfo.get("password"),
                registerInfo.get("email"));
        InMemoryUserRepository.save(newUser);
        response.addHeader(HttpHeader.LOCATION.getName(), "index.html");
    }
}
