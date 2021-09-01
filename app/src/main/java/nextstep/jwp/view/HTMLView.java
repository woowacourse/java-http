package nextstep.jwp.view;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;

import java.io.IOException;

public class HTMLView extends View {

    @Override
    public void render(ModelAndView mv, HttpRequest request, HttpResponse response) throws IOException {
        String responseBody = resolveResponseBody(mv.getViewName());
        response.setContentLength(responseBody.getBytes().length);
        responseBody = processResponseAndBody(response, responseBody);
        write(response.getOutputStream(), responseBody);
    }
}
