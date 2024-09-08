package com.techcourse.executor;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.file.ResourcesReader;
import org.apache.coyote.http11.executor.Executor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.ResourceToResponseConverter;
import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterPostExecutor implements Executor {
    @Override
    public HttpResponse execute(final HttpRequest request) {
        final String account = request.getBodyAttribute("account");
        final String email = request.getBodyAttribute("email");
        final String password = request.getBodyAttribute("password");

        InMemoryUserRepository.save(new User(account, password, email));
        return ResourceToResponseConverter.convert(HttpStatusCode.FOUND, ResourcesReader.read(Path.from("index.html")));
    }

    @Override
    public boolean isMatch(final HttpRequest request) {
        return request.getMethod() == HttpMethod.POST && request.getPath()
                .equals("/register");
    }
}
