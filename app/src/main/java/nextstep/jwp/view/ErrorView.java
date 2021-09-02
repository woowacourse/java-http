package nextstep.jwp.view;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.model.httpmessage.response.HttpStatus;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;

public class ErrorView extends View {

    private HttpStatus status;

    public void sendError(HttpStatus status) {
        this.status = status;
    }

    @Override
    public void render(ModelAndView mv, HttpRequest request, HttpResponse response) throws IOException {
        response.setStatus(status);
        response.setContentType(HTML.value());

        String responseBody = resolveResponseBody(mv.getViewName());
        response.setContentLength(responseBody.getBytes().length);

        responseBody = processResponseAndBody(response, responseBody);
        write(response.getOutputStream(), responseBody);
    }
}
