package org.apache.coyote;

import java.io.IOException;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public interface Adapter {

    void service(HttpRequest request, HttpResponse response) throws IOException;
}
