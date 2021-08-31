package nextstep.mockweb.option;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.mvc.DispatcherServlet;
import nextstep.mockweb.result.MockResult;
import nextstep.mockweb.request.RequestInfo;

public class MockOption {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_DATA = "application/x-www-form-urlencoded";

    private final RequestInfo requestInfo;
    private final OptionInfo optionInfo;
    private final DispatcherServlet dispatcherServlet;

    public MockOption(RequestInfo requestInfo, DispatcherServlet dispatcherServlet) {
        this.requestInfo = requestInfo;
        this.optionInfo = new OptionInfo();
        this.dispatcherServlet = dispatcherServlet;
    }

    public MockResult result() {
        String result = doRequest(requestInfo, optionInfo);
        return new MockResult(result);
    }

    public MockOption logAll() {
        optionInfo.logAll();
        return this;
    }

    public MockOption addFormData(String key, String value) {
        requestInfo.addFormData(key, value);
        addHeader(CONTENT_TYPE, FORM_DATA);
        return this;
    }

    public MockOption addHeader(String key, String value) {
        requestInfo.addHeader(key, value);
        return this;
    }

    private String doRequest(RequestInfo requestInfo, OptionInfo optionInfo) {
        final MockSocket mockSocket = new MockSocket(requestInfo.asRequest());
        optionInfo.executeBeforeOption(requestInfo);
        new RequestHandler(mockSocket, dispatcherServlet).run();
        final String result = mockSocket.output();
        optionInfo.executeAfterOption(result);
        return result;
    }
}
