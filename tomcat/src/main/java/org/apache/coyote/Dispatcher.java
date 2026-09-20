package org.apache.coyote;

import java.io.IOException;
import org.apache.catalina.Session;

public interface Dispatcher {

    HttpResponse dispatch(HttpRequest httpRequest, Session session) throws IOException;
}
