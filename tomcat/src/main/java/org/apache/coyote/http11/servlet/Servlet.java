package org.apache.coyote.http11.servlet;

import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.view.View;

public interface Servlet {

    Optional<View> service(HttpRequest request);
}
