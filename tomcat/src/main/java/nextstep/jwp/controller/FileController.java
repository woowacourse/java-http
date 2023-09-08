package nextstep.jwp.controller;

import java.util.List;
import org.apache.common.FileReader;
import org.apache.common.HttpMethod;
import org.apache.handler.AbstractController;
import org.apache.request.HttpRequest;
import org.apache.response.ContentType;
import org.apache.response.HttpResponse;
import org.apache.response.HttpStatus;

public class FileController extends AbstractController {

    private static final List<HttpMethod> ALLOW_METHOD = List.of(HttpMethod.GET);
    private static final String NOT_FOUND_PAGE = "/405.html";
    private static final String METHOD_NOT_ALLOWED_PAGE = "/405.html";

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String target = httpRequest.getPath();
        if (isNotFoundPage(target)) {
            String content = FileReader.read(NOT_FOUND_PAGE);
            httpResponse.setHttpStatus(HttpStatus.NOT_FOUND);
            httpResponse.setContentType(ContentType.from(target));
            httpResponse.setBody(content);
            return;
        }
        String content = FileReader.read(target);
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setContentType(ContentType.from(target));
        httpResponse.setBody(content);
    }

    private boolean isNotFoundPage(String target) {
        return FileReader.parseURL(target) == null;
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
