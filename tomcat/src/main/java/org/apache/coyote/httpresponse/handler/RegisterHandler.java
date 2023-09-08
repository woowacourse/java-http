package org.apache.coyote.httpresponse.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.RequestBody;
import org.apache.coyote.httprequest.RequestMethod;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;
import org.apache.coyote.httpresponse.handler.util.RequestBodyParser;

public class RegisterHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final RequestMethod requestMethod = request.getRequestMethod();
        if (requestMethod == RequestMethod.POST) {
            return handlePost(request);
        }
        if (requestMethod == RequestMethod.GET) {
            return handleGet(request);
        }
        return new MethodNotAllowedHandler().handle(request);
    }

    private HttpResponse handlePost(final HttpRequest request) {
        saveUser(request.getRequestBody());
        return HttpResponse
                .init(request.getHttpVersion())
                .setHttpStatus(HttpStatus.CREATED)
                .setLocationHeader("/index.html");
    }

    private void saveUser(final RequestBody requestBody) {
        final User user = RequestBodyParser.parse(requestBody);
        InMemoryUserRepository.save(user);
    }

    private HttpResponse handleGet(final HttpRequest request) {
        final String resourcePath = request.getPath() + ".html";
        return HttpResponse
                .init(request.getHttpVersion())
                .setHttpStatus(HttpStatus.OK)
                .setContent(resourcePath);
    }
}
