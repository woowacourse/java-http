package org.apache.coyote.http11.servlet;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.request.QueryParams;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.util.Parser;
import org.apache.coyote.http11.util.StaticFileLoader;

public class RegisterServlet extends Servlet {

    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        String content = StaticFileLoader.load(Page.REGISTER.getUri());

        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.TEXT_HTML);
        response.setContentLength(content.getBytes().length);
        response.setBody(content);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        QueryParams params = Parser.parseToQueryParams(request.getBody().getContent());
        String account = params.getParam(ACCOUNT);
        String password = params.getParam(PASSWORD);
        String email = params.getParam(EMAIL);

        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new BadRequestException();
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.setStatusCode(StatusCode.FOUND);
        response.setLocation(Page.INDEX);
    }
}
