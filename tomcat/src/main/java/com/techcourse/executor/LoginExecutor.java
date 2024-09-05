package com.techcourse.executor;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.file.ResourcesReader;
import org.apache.coyote.http11.Executor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Path;
import org.apache.coyote.http11.ResourceToResponseConverter;
import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginExecutor implements Executor {
    @Override
    public HttpResponse execute(final HttpRequest request) {
        final String account = request.getQueryParam("account");
        final String password = request.getQueryParam("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> ResourceToResponseConverter.convert(HttpStatusCode.FOUND, ResourcesReader.read(Path.from("index.html"))))
                .orElseGet(() -> ResourceToResponseConverter.convert(HttpStatusCode.UNAUTHORIZED, ResourcesReader.read(Path.from("401.html"))));
    }

    @Override
    public boolean isMatch(final HttpRequest request) {
        return request.getMethod() == HttpMethod.GET && request.getPath()
                .equals("/login");
    }
}
