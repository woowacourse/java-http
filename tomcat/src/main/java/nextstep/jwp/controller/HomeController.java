package nextstep.jwp.controller;

import nextstep.jwp.exception.InvalidRequestMethodException;
import nextstep.jwp.http.common.ContentType;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class HomeController extends AbstractController {

    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        throw new InvalidRequestMethodException("지원하지 않는 메서드입니다.");
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setBody(DEFAULT_MESSAGE);
        response.setContentType(ContentType.extractValueFromPath(request.getNativePath()));
    }
}
