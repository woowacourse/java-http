package nextstep.jwp.controller;


import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.model.User;

public class AbstractController implements Controller{

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.checkMethod("GET")) {
            doGet(httpRequest, httpResponse);
        }

        if (httpRequest.checkMethod("POST")) {
            doPost(httpRequest, httpResponse);
        }
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }

    protected User getUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        return (User) session.getAttribute("user");
    }

    protected boolean isLogin(HttpSession httpSession) {
        Object user = getUser(httpSession);
        return user != null;
    }
}
