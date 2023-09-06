package org.apache.coyote.http11.response;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.QueryParams;
import org.apache.coyote.http11.request.HttpRequest;

public class RegisterPostResponseMaker extends ResponseMaker {

    @Override
    public String createResponse(final HttpRequest request) {
        saveMemberFromQuery(request);

        final HttpResponse httpResponse = new HttpResponse(StatusCode.FOUND);
        return httpResponse.getRedirectResponse("/index.html");
    }

    private void saveMemberFromQuery(final HttpRequest request) {
        final QueryParams queryParams = QueryParams.from(request.getRequestBody());
        InMemoryUserRepository.save(new User(
                queryParams.getValueFromKey("account"),
                queryParams.getValueFromKey("password"),
                queryParams.getValueFromKey("email")));
    }

}
