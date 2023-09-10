package nextstep.org.apache.catalina.servlet;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.org.apache.coyote.http11.request.Http11Request;
import nextstep.org.apache.coyote.http11.response.Http11Response;
import nextstep.org.apache.coyote.http11.Status;

public class RegisterServlet extends AbstractServlet{

    @Override
    protected void doPost(Http11Request request, Http11Response response) {

        InMemoryUserRepository.save(
                new User(
                        request.getParsedBodyValue("account"),
                        request.getParsedBodyValue("password"),
                        request.getParsedBodyValue("email")
                )
        );

        response.setStatus(Status.FOUND)
                .setHeader("Location", "/index.html");
    }

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        responseWithBody(request, response);
    }
}
