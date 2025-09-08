package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterPostRequestHandler implements HttpRequestHandler {
    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.POST &&
                httpRequest.getRequestUrl().equals("/register");
    }

    @Override
    public String response(final HttpRequest httpRequest) {
        String account = httpRequest.getParameter("account");
        String password = httpRequest.getParameter("password");
        String email = httpRequest.getParameter("email");
        InMemoryUserRepository.save(new User(account, password, email));

        return createRedirectResponse("http://localhost:8080/index.html");
    }

    private String createRedirectResponse(final String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Length: " + 0 + " ",
                "Location: " + redirectUrl + " ",
                "");
    }
}
