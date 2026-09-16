package org.apache.coyote;

import java.io.IOException;
public interface Dispatcher {

    HttpResponse dispatch(HttpRequest httpRequest) throws IOException;
}
