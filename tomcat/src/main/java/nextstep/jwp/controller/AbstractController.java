package nextstep.jwp.controller;

import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

public abstract class AbstractController implements Controller{

    @Override
    public abstract HttpResponse service(final HttpRequest httpRequest);

    protected abstract HttpResponse doGet(final HttpRequest httpRequest);

    protected abstract HttpResponse doPost(final HttpRequest httpRequest);
}
