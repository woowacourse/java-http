package org.apache.coyote;

import java.io.IOException;
import org.apache.catalina.Session;

public interface Adapter {

    HttpResponse service(HttpRequest httpRequest, Session session) throws IOException;
}
