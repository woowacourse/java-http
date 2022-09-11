package nextstep.jwp.controller;

import org.apache.coyote.http11.model.response.HttpResponse;

public abstract class AbstractController implements Controller{

    @Override
    public abstract HttpResponse service();

    protected abstract HttpResponse doGet();

    protected abstract HttpResponse doPost();
}
