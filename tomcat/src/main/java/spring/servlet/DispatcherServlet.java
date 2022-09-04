package spring.servlet;

import org.apache.coyote.RegisterAsServlet;
import org.apache.coyote.Servlet;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

@RegisterAsServlet
public class DispatcherServlet implements Servlet {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        return null;
    }
}
