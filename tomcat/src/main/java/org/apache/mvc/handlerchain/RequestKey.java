package org.apache.mvc.handlerchain;

import java.util.Objects;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.mvc.annotation.RequestMapping;

public class RequestKey {

    private final RequestMethod requestMethod;
    private final String requestPath;

    public RequestKey(RequestMethod requestMethod, String requestPath) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
    }

    public static RequestKey from(RequestMapping annotation) {
        return new RequestKey(
                annotation.method(),
                annotation.value()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestKey that = (RequestKey) o;
        return requestMethod == that.requestMethod && Objects.equals(requestPath, that.requestPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestMethod, requestPath);
    }
}
