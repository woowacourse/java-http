package nextstep.mockweb.request;

import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.core.DefaultApplicationContext;
import nextstep.jwp.core.mvc.FrontHandler;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.mockweb.option.MockOption;

public class MockRequest {

    private static final FrontHandler frontHandler = new FrontHandler("nextstep");

    public static MockOption get(String url){
        return new MockOption(new RequestInfo(HttpMethod.GET, url, null), frontHandler);
    }

    public static MockOption post(String url, Object body) {
        return new MockOption(new RequestInfo(HttpMethod.POST, url, body), frontHandler);
    }

    public static MockOption post(String url) {
        return new MockOption(new RequestInfo(HttpMethod.POST, url, null), frontHandler);
    }
}
