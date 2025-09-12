package org.apache.coyote.http.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RegisterController extends FrontController {

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        String url = request.getRequestLine().getUrl();
        final URL resource = getClass().getClassLoader().getResource("static" + url + ".html");
        validateNullResource(resource);
        final String responseBody = Files.readString(Paths.get(resource.toURI()));
        return HttpResponse.ok(responseBody, TEXT_HTML_CHARSET_UTF_8);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        String body = request.getBody();
        final String[] queryStringParts = body.split("&");
        final String account = queryStringParts[0].split("=")[1];
        final String email = queryStringParts[1].split("=")[1];
        final String password = queryStringParts[2].split("=")[1];

        final User user = createUser(account, password, email);
        InMemoryUserRepository.save(user);
        return HttpResponse.redirection("index.html", TEXT_HTML_CHARSET_UTF_8);
    }

    private User createUser(String account, String password, String email) {
        Long id = 1L;
        return new User(++id, account, password, email);
    }

    private void validateNullResource(final URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 resource 입니다.");
        }
    }

}
