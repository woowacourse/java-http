package org.apache.catalina;

import java.util.Optional;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class FakeRequestMapper implements Mapper {

    @Override
    public Optional<Controller> getController(HttpRequest request) {
        return Optional.empty();
    }
}
