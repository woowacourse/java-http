package nextstep.jwp.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.exception.MethodMappingFailException;
import org.apache.front.AbstractController;

public class HelloWorldController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new MethodMappingFailException();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setStringAsBody("Hello world!");
    }
}
