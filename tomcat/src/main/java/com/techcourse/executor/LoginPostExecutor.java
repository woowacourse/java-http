package com.techcourse.executor;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.*;
import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.executor.Executor;
import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;

public class LoginPostExecutor implements Executor {

    @Override
    public HttpResponse execute(final HttpRequest request) {
        if (request.existSessionAttribute("user")) {
            return movePageResponse();
        }
        final String account = request.getBodyAttribute("account");
        final String password = request.getBodyAttribute("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> login(request, user))
                .orElseGet(() -> ResourceToResponseConverter.redirect(HttpStatusCode.FOUND, Path.from("401.html")));
    }

    @Override
    public boolean isMatch(final HttpRequest request) {
        return request.getMethod() == HttpMethod.POST && request.getPath()
                .equals("/login");
    }

    private HttpResponse login(final HttpRequest request, final User user) {
        final Session session = request.getSession();
        session.setAttribute("user", user);
        final HttpResponse response = movePageResponse();
        response.addCookie(Cookies.SESSION_ID, session.getId());
        return response;
    }

    private HttpResponse movePageResponse() {
        return ResourceToResponseConverter.redirect(HttpStatusCode.FOUND, Path.from("index.html"));
    }
}
