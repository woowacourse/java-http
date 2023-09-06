package org.apache.coyote;

import java.io.IOException;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.Response;

public interface Container {

    boolean supports(Request request);

    Response service(final Request request) throws IOException;

    void addHandler(final Handler handler);
}
