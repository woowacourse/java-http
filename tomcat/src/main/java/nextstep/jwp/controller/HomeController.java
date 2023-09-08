package nextstep.jwp.controller;

import java.util.List;
import org.apache.common.FileReader;
import org.apache.common.HttpMethod;
import org.apache.handler.AbstractController;
import org.apache.request.HttpRequest;
import org.apache.response.ContentType;
import org.apache.response.HttpResponse;
import org.apache.response.HttpStatus;

public class HomeController extends AbstractController {

    private static final String DEFAULT_RESPONSE = "Hello world!";
    private static final String METHOD_NOT_ALLOWED_PAGE = "/405.html";
    private static final List<HttpMethod> ALLOW_METHOD = List.of(HttpMethod.GET);

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        httpResponse.setBody(DEFAULT_RESPONSE);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse httpResponse) throws Exception {
        String content = FileReader.read(METHOD_NOT_ALLOWED_PAGE);
        httpResponse.setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
        httpResponse.setAllow(ALLOW_METHOD);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        httpResponse.setBody(content);
    }
}
