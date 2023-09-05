package org.apache.coyote.http11.request;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.response.OK;
import org.apache.coyote.http11.response.Redirect;
import org.apache.coyote.http11.response.Response;

import static org.apache.coyote.http11.request.Method.POST;
import static org.apache.coyote.http11.response.Resource.INDEX;
import static org.apache.coyote.http11.response.Resource.LOGIN;
import static org.apache.coyote.http11.response.Resource.REGISTER;
import static org.apache.coyote.http11.response.Resource.UNAUTHORIZED;

public class Request {

    private static final String JAVA_SESSION_ID = "JSESSIONID";

    private final Location location;
    private final Method method;
    private final Parameters parameters;
    private final Cookies cookies;

    public Request(final Location location, final Method method, final Parameters parameters, final Cookies cookies) {
        this.location = location;
        this.method = method;
        this.parameters = parameters;
        this.cookies = cookies;
    }

    public Response handle() {
        if (method.equals(POST)) {
            return post();
        }

        return get();
    }

    private Response post() {
        if (location.is(REGISTER.path())) {
            UserService.register(parameters);
            return new Redirect(INDEX.path());
        }

        if (location.is(LOGIN.path())) {
            try {
                final User user = UserService.login(parameters);
                UserSession.login(cookies.getValue(JAVA_SESSION_ID), user);
                return new Redirect(INDEX.path());
            } catch (Exception e) {
                return new Redirect(UNAUTHORIZED.path());
            }
        }

        throw new IllegalArgumentException("지원하지 않는 메서드입니다.");
    }

    private Response get() {
        if (location.is(LOGIN.path()) && UserSession.exist(cookies.getValue(JAVA_SESSION_ID))) {
            return new Redirect(INDEX.path());
        }

        return new OK(location, cookies);
    }
}
