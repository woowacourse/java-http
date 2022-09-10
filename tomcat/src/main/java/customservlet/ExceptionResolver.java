package customservlet;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public interface ExceptionResolver {

    void resolveException(HttpRequest httpRequest, HttpResponse httpResponse);
}
