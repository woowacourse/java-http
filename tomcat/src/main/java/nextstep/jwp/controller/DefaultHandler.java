package nextstep.jwp.controller;

import org.apache.coyote.model.request.ContentType;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseLine;
import org.apache.coyote.model.response.StatusCode;
import org.apache.coyote.utils.Util;

import static org.apache.coyote.utils.Util.getExtension;

public class DefaultHandler extends AbstractHandler {

    public DefaultHandler(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public String getResponse() {
        final String path = httpRequest.getPath();
        final String extension = ContentType.getType(getExtension(path));
        final String body = Util.getResponseBody(path, this.getClass());

        return HttpResponse.of(extension, body, ResponseLine.of(StatusCode.OK))
                .getResponse();
    }
}
