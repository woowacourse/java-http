package org.apache.catalina;

import java.util.Optional;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public interface Mapper {

    Optional<Controller> getController(HttpRequest request);
}
