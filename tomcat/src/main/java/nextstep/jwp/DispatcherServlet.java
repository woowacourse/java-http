package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpServlet;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DispatcherServlet implements HttpServlet {

    private static final HandlerMapping handlerMapping = new HandlerMapping();

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Controller controller = handlerMapping.findController(httpRequest.getPath());
        if (controller == null) {
            generateNotFoundController(httpResponse);
            return;
        }
        controller.service(httpRequest, httpResponse);
    }

    private void generateNotFoundController(final HttpResponse response) {
        response.setStatusCode(StatusCode.NOT_FOUND)
                .setContentType(ContentType.HTML)
                .setRedirect("/404.html");
    }
}
