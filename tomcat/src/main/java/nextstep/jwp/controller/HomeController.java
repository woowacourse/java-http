package nextstep.jwp.controller;

import nextstep.jwp.exception.InvalidRequestMethodException;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomeController extends AbstractController {

    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        throw new InvalidRequestMethodException("지원하지 않는 메서드입니다.");
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setContentType(ContentType.extractValueFromPath(request.getNativePath()));
        response.setBody(DEFAULT_MESSAGE);
    }
}
