package nextstep.mockweb.request;

import nextstep.jwp.core.DefaultApplicationContext;
import nextstep.jwp.mvc.DispatcherServlet;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.mockweb.option.MockOption;

public class MockRequest {

    private static final DispatcherServlet dispatcherServlet = new DispatcherServlet(new DefaultApplicationContext("nextstep"));

    public static MockOption get(String url){
        return new MockOption(new RequestInfo(HttpMethod.GET, url, null), dispatcherServlet);
    }

    public static MockOption post(String url, Object body) {
        return new MockOption(new RequestInfo(HttpMethod.POST, url, body), dispatcherServlet);
    }

    public static MockOption post(String url) {
        return new MockOption(new RequestInfo(HttpMethod.POST, url, null), dispatcherServlet);
    }
}
