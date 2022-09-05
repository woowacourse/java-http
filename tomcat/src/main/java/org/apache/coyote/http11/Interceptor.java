package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;

public interface Interceptor {
    boolean handle(HttpRequest httpRequest) throws IOException;
}
