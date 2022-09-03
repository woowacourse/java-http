package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.FileReader;

public class LoginController implements Controller {

    private static final int QUERY_KEY_INDEX = 0;
    private static final int QUERY_VALUE_INDEX = 1;

    @Override
    public HttpResponse doService(HttpRequest request) {

        String uri = request.getUri();

        if (uri.equals("/login")) {
            return HttpResponse.withoutLocation(
                request.getVersion(),
                "200 OK",
                request.getUri(),
                FileReader.read("/login.html")
            );
        }

        String queryString = uri.substring(uri.indexOf('?') + 1);
        Map<String, String> parsedQuery = parseQueryString(queryString);

        String account = parsedQuery.get("account");
        String password = parsedQuery.get("password");

        User user = InMemoryUserRepository.findByAccount(account).get();
        System.out.println(user);

        if (user.checkPassword(password)) {
            return HttpResponse.withLocation(
                request.getVersion(),
                "302 Found",
                request.getUri(),
                "/index.html",
                ""
            );
        } else {
            return HttpResponse.withLocation(
                request.getVersion(),
                "302 Found",
                request.getUri(),
                "/401.html",
                ""
            );
        }

    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> queryMap = new HashMap<>();

        for (String q : query.split("&")) {
            String[] parsedQuery = q.split("=");
            queryMap.put(parsedQuery[QUERY_KEY_INDEX], parsedQuery[QUERY_VALUE_INDEX]);
        }

        return queryMap;
    }
}
