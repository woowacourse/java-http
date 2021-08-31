package nextstep.mockweb.request;

import nextstep.jwp.core.DefaultApplicationContext;
import nextstep.jwp.mvc.DispatcherServlet;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.request.HttpSessions;
import nextstep.mockweb.option.MockOption;

public class MockRequest {

    private static final DispatcherServlet dispatcherServlet = new DispatcherServlet(new DefaultApplicationContext("nextstep"));
    private static final HttpSessions httpSessions = new HttpSessions();

    public static MockOption get(String url){
        return new MockOption(new RequestInfo(HttpMethod.GET, url, null), dispatcherServlet, httpSessions);
    }

    public static MockOption post(String url, Object body) {
        return new MockOption(new RequestInfo(HttpMethod.POST, url, body), dispatcherServlet, httpSessions);
    }

    public static MockOption post(String url) {
        return new MockOption(new RequestInfo(HttpMethod.POST, url, null), dispatcherServlet, httpSessions);
    }
}
