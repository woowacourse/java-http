package org.apache.coyote.http11.executor;

import com.techcourse.executor.ResourceExecutor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.List;

public class RequestExecutors {
    private final List<Executor> executors;
    private final Executor pageExecutor = new ResourceExecutor();

    public RequestExecutors(final List<Executor> executors) {
        this.executors = executors;
    }

    public HttpResponse execute(final HttpRequest req) {
        return executors.stream()
                .filter(executor -> executor.isMatch(req))
                .findFirst()
                .map(executor -> executor.execute(req))
                .orElseGet(() -> pageExecutor.execute(req));
    }
}

