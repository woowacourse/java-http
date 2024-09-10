package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.QueryParameter;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;
import org.apache.coyote.http11.utils.Constants;

public class RegisterController implements Controller {

    @Override
    public HttpResponse process(HttpRequest request) {
        if (request.isGetMethod()) {
            String location = request.getUri() + Constants.EXTENSION_OF_HTML;
            return HttpResponse.of(location, HttpStatusCode.OK);
        }
        if (request.isPostMethod()) {
            User user = findUser(request.getQueryParameter());
            InMemoryUserRepository.save(user);

            return HttpResponse.of(Constants.DEFAULT_URI, HttpStatusCode.OK);
        }
        return null;
    }

    private User findUser(QueryParameter queryParameter) {
        String account = queryParameter.get(Constants.PARAMETER_KEY_OF_ACCOUNT);
        String password = queryParameter.get(Constants.PARAMETER_KEY_OF_PASSWORD);
        String email = queryParameter.get(Constants.PARAMETER_KEY_OF_EMAIL);

        return new User(account, password, email);
    }

}
