package nextstep.mockweb.option;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.core.DefaultApplicationContext;
import nextstep.jwp.core.handler.FrontHandler;
import nextstep.mockweb.result.MockResult;
import nextstep.mockweb.request.RequestInfo;

public class MockOption {

    private final RequestInfo requestInfo;
    private final OptionInfo optionInfo;

    public MockOption(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
        this.optionInfo = new OptionInfo();
    }

    public MockResult result() {
        String result = doRequest(requestInfo, optionInfo);
        return new MockResult(result);
    }

    public MockOption logAll() {
        optionInfo.logAll();
        return this;
    }

    public MockOption addHeader(String key, String value) {
        optionInfo.addHeader(key, value);
        return this;
    }

    private String doRequest(RequestInfo requestInfo, OptionInfo optionInfo) {
        final MockSocket mockSocket = new MockSocket(requestInfo.asRequest());
        optionInfo.executeBeforeOption(requestInfo);
        new RequestHandler(mockSocket, new FrontHandler("nextstep")).run();
        final String result = mockSocket.output();
        optionInfo.executeAfterOption(result);
        return result;
    }
}
