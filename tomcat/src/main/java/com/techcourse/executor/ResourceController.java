package com.techcourse.executor;

import com.techcourse.controller.AbstractController;
import org.apache.coyote.file.ResourcesReader;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.ResourceToResponseConverter;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController extends AbstractController {
    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        try {
            return ResourceToResponseConverter.convert(HttpStatusCode.OK, ResourcesReader.read(Path.from(request.getPath())));
        } catch (final Exception e) {
            return ResourceToResponseConverter.convert(HttpStatusCode.NOT_FOUND, ResourcesReader.read(Path.from("404.html")));
        }
    }
}
