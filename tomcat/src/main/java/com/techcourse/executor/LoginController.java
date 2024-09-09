package com.techcourse.executor;

import com.techcourse.controller.AbstractController;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.file.ResourcesReader;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.ResourceToResponseConverter;
import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;

public class LoginController extends AbstractController {
    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return ResourceToResponseConverter.convert(HttpStatusCode.OK, ResourcesReader.read(Path.from("login.html")));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
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
