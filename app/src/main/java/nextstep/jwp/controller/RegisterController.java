package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.infrastructure.http.objectmapper.DataMapper;
import nextstep.jwp.infrastructure.http.objectmapper.UrlEncodingMapper;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.ResponseLine;
import nextstep.jwp.infrastructure.http.response.StatusCode;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    private static final DataMapper DATA_MAPPER = new UrlEncodingMapper();
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    public String uri() {
        return "/register";
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setResponseLine(new ResponseLine(StatusCode.OK));
        respondByFile("/register.html", response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> body = DATA_MAPPER.parse(request.getMessageBody());
        validateBody(request, body);
        final User user = new User(body.get(ACCOUNT), body.get(PASSWORD), body.get(EMAIL));
        InMemoryUserRepository.save(user);

        response.setResponseLine(new ResponseLine(StatusCode.FOUND));
        response.addHeader("Location", "/index.html");
    }

    private void validateBody(final HttpRequest request, final Map<String, String> body) {
        if (!body.containsKey(ACCOUNT) || !body.containsKey(PASSWORD) || !body.containsKey(EMAIL)) {
            throw new IllegalArgumentException(String.format("Invalid body format. (%s)", request.getMessageBody()));
        }
    }
}
