package nextstep.jwp.commandcontroller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.common.ContentType;
import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.StatusLine;

public class RegisterController extends AbstractController{

    @Override
    public boolean canHandle(HttpRequest request) {
        final HttpMethod method = request.getHttpMethod();
        final String uri = request.getRequestUri();
        return method.equals(HttpMethod.POST) && uri.equals("register");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response)  {
        final Map<String, String> registerInfo = Arrays.stream(request.getRequestBody().split("&"))
                .map(input -> input.split("="))
                .collect(Collectors.toMap(info -> info[0], info -> info[1]));

        response.setStatusLine(StatusLine.of(request.getHttpVersion(), HttpStatus.FOUND));
        if (InMemoryUserRepository.findByAccount(registerInfo.get("account")).isPresent()) {
            response.addHeader("Location", "register.html");
            return;
        }

        final User newUser = new User(registerInfo.get("account"), registerInfo.get("password"),
                registerInfo.get("email"));
        InMemoryUserRepository.save(newUser);
        response.addHeader("Location", "index.html");
    }
}
