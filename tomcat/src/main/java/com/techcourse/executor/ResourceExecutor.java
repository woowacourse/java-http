package com.techcourse.executor;

import org.apache.coyote.file.ResourcesReader;
import org.apache.coyote.http11.Executor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Path;
import org.apache.coyote.http11.ResourceToResponseConverter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceExecutor implements Executor {
    @Override
    public HttpResponse execute(final HttpRequest req) {
        try {
            return ResourceToResponseConverter.convert(HttpStatusCode.OK, ResourcesReader.read(Path.from(req.getPath())));
        } catch (Exception e) {
            return ResourceToResponseConverter.convert(HttpStatusCode.NOT_FOUND, ResourcesReader.read(Path.from("404.html")));
        }

    }

    @Override
    public boolean isMatch(final HttpRequest req) {
        return false;
    }
}
