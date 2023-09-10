package org.apache.coyote;

import java.io.IOException;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;

public interface Handler {

    void handle(HttpRequest request, HttpResponse response) throws IOException;
}
