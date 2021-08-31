package nextstep.mockweb.request;

import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.mockweb.option.MockOption;

public class MockRequest {

//    private static final FrontHandler frontHandler = new FrontHandler("nextstep");

    public static MockOption get(String url){
        return new MockOption(new RequestInfo(HttpMethod.GET, url, null));
    }

    public static MockOption post(String url, Object body) {
        return new MockOption(new RequestInfo(HttpMethod.POST, url, body));
    }

    public static MockOption post(String url) {
        return new MockOption(new RequestInfo(HttpMethod.POST, url, null));
    }
}
