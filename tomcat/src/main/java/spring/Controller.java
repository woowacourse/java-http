package spring;

import org.apache.http.HttpRequest;

public interface Controller {

    boolean support(String requestUri);

    String service(final HttpRequest httpRequest);
}
