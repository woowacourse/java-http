package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterPostRequestHandler implements HttpRequestHandler {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.POST &&
                httpRequest.getRequestUrl()
                        .equals("/register");
    }

    @Override
    public void response(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String account = httpRequest.getParameter("account");
        String password = httpRequest.getParameter("password");
        String email = httpRequest.getParameter("email");
        InMemoryUserRepository.save(new User(account, password, email));

        httpResponse.redirect("http://localhost:8080/index.html");
    }
}
