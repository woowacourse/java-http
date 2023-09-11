package org.apache.coyote.http11.response;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.QueryParams;
import org.apache.coyote.http11.request.HttpRequest;

public class RegisterPostController implements Controller {

    @Override
    public void service(final HttpRequest request,
                        final HttpResponse response) {
        saveMemberFromQuery(request);

        response.setStatusCode(StatusCode.FOUND);
        response.addHeader("Location", "/index.html");
    }

    private void saveMemberFromQuery(final HttpRequest request) {
        final QueryParams queryParams = QueryParams.from(request.getRequestBody());
        InMemoryUserRepository.save(new User(
                queryParams.getValueFromKey("account"),
                queryParams.getValueFromKey("password"),
                queryParams.getValueFromKey("email")));
    }

}
