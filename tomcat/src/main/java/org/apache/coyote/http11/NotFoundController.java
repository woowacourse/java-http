package org.apache.coyote.http11;

import org.apache.coyote.http11.constant.ContentType;
import org.apache.coyote.http11.constant.HttpStatus;

public class NotFoundController extends AbstractController {

    public NotFoundController() {
        super("notFound");
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatusCode(HttpStatus.NOT_FOUND);
        httpResponse.setContentType(ContentType.TEXT);
    }
}
