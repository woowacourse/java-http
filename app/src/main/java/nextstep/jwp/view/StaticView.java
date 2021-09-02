package nextstep.jwp.view;

import nextstep.jwp.model.httpmessage.common.ContentType;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;

public class StaticView extends View {

    private final String viewPath;

    public StaticView(String absolutePath) {
        this.viewPath = absolutePath;
    }

    @Override
    public void render(ModelAndView mv, HttpRequest request, HttpResponse response) throws IOException {
        response.setStatus(OK);
        ContentType contentType = ContentType.of(viewPath)
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 정적 파일입니다. (url : " + viewPath + ")"));
        response.setContentType(contentType.value());

        String responseBody = resolveResponseBody(viewPath);
        response.setContentLength(responseBody.getBytes().length);

        responseBody = processResponseAndBody(response, responseBody);
        write(response.getOutputStream(), responseBody);
    }
}
