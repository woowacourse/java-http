package nextstep.jwp.web.handler;

import java.io.IOException;
import nextstep.jwp.web.StaticResourceReader;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.ContentType;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.StatusCode;

public class StaticFileRequestHandlerImpl implements StaticFileRequestHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        String targetResource = request.getUrl();
        ContentType contentType = ContentType.findContentType(
            targetResource.substring(targetResource.indexOf("."))
        );

        String responseBody =
            new StaticResourceReader(targetResource).content();

        response.setStatusLine(StatusCode.OK);
        response.addHeader("Content-Type", contentType.getValue());
        response.addHeader("Content-Length", responseBody.getBytes().length + " ");
        response.addBody(responseBody);
    }
}
