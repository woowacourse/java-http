package com.techcourse.executor;

import org.apache.coyote.file.ResourcesReader;
import org.apache.coyote.http11.executor.Executor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.ResourceToResponseConverter;
import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterGetExecutor implements Executor {
    @Override
    public HttpResponse execute(final HttpRequest request) {
        return ResourceToResponseConverter.convert(HttpStatusCode.OK, ResourcesReader.read(Path.from("register.html")));
    }

    @Override
    public boolean isMatch(final HttpRequest request) {
        return request.getMethod() == HttpMethod.GET && request.getPath()
                .equals("/register");
    }
}
