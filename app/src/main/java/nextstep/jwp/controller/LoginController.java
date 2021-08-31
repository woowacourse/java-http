package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.infrastructure.http.objectmapper.DataMapper;
import nextstep.jwp.infrastructure.http.objectmapper.UrlEncodingMapper;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.ResponseLine;
import nextstep.jwp.infrastructure.http.response.StatusCode;

public class LoginController extends AbstractController {

    private static final DataMapper DATA_MAPPER = new UrlEncodingMapper();
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final List<String> REQUIRED_PARAMETERS = Arrays.asList(ACCOUNT, PASSWORD);

    @Override
    public String uri() {
        return "/login";
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setResponseLine(new ResponseLine(StatusCode.OK));
        respondByFile("/login.html", response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> body = DATA_MAPPER.parse(request.getMessageBody());
        if (!containsAllKey(body)) {
            redirect("/500.html", response);
            return;
        }

        redirect(locationByLogin(body), response);
    }

    private boolean containsAllKey(final Map<String, String> body) {
        return REQUIRED_PARAMETERS.stream()
            .allMatch(body::containsKey);
    }

    public String locationByLogin(final Map<String, String> body) {
        final String account = body.get(ACCOUNT);
        final String password = body.get(PASSWORD);

        if (InMemoryUserRepository.existsByAccountAndPassword(account, password)) {
            return "/index.html";
        }
        return "/401.html";
    }

    private void redirect(final String location, final HttpResponse response) {
        response.setResponseLine(new ResponseLine(StatusCode.FOUND));
        response.addHeader("Location", location);
    }
}
