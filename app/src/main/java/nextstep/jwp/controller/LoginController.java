package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemorySessionRepository;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.infrastructure.http.objectmapper.DataMapper;
import nextstep.jwp.infrastructure.http.objectmapper.UrlEncodingMapper;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.ResponseLine;
import nextstep.jwp.infrastructure.http.response.StatusCode;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final DataMapper DATA_MAPPER = new UrlEncodingMapper();
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final List<String> REQUIRED_PARAMETERS = Arrays.asList(ACCOUNT, PASSWORD);
    private static final String SESSION_KEY = "JSESSIONID";

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
        final Optional<String> sessionId = request.getCookie(SESSION_KEY);

        if (sessionId.isEmpty() || !containsAllKey(body)) {
            redirect("/401.html", response);
            return;
        }

        redirect(locationByLogin(body, sessionId.get()), response);
    }

    private boolean containsAllKey(final Map<String, String> body) {
        return REQUIRED_PARAMETERS.stream()
            .allMatch(body::containsKey);
    }

    private String locationByLogin(final Map<String, String> body, String sessionId) {
        final String account = body.get(ACCOUNT);
        final String password = body.get(PASSWORD);

        if (!InMemoryUserRepository.existsByAccountAndPassword(account, password)) {
            return "/401.html";
        }
        final User user = InMemoryUserRepository.findByAccount(account);
        final Session session = InMemorySessionRepository.getSession(sessionId);
        session.setAttribute("user", user);

        return "/index.html";
    }

    private void redirect(final String location, final HttpResponse response) {
        response.setResponseLine(new ResponseLine(StatusCode.FOUND));
        response.addHeader("Location", location);
    }
}
