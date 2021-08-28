package nextstep.mockweb.request;

import nextstep.jwp.request.HttpMethod;
import nextstep.mockweb.option.MockOption;

public class MockRequest {

    public static MockOption get(String url){
        return new MockOption(new RequestInfo(HttpMethod.GET, url, null));
    };
}
