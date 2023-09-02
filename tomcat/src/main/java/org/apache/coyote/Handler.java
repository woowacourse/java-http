package org.apache.coyote;

import java.io.IOException;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;

public interface Handler {

    HttpResponse handle(HttpRequest request) throws IOException;
}
