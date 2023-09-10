package nextstep.servlet;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Servlet {

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
