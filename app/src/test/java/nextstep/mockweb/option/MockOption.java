package nextstep.mockweb.option;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.mvc.DispatcherServlet;
import nextstep.jwp.webserver.request.HttpSessions;
import nextstep.mockweb.request.RequestInfo;
import nextstep.mockweb.result.MockResult;

public class MockOption {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_DATA = "application/x-www-form-urlencoded";
    private static String sessionId = "";

    private final RequestInfo requestInfo;
    private final OptionInfo optionInfo;
    private final DispatcherServlet dispatcherServlet;
    private final HttpSessions httpSessions;

    public MockOption(RequestInfo requestInfo, DispatcherServlet dispatcherServlet, HttpSessions httpSessions) {
        this.requestInfo = requestInfo;
        this.optionInfo = new OptionInfo();
        this.dispatcherServlet = dispatcherServlet;
        this.httpSessions = httpSessions;
        httpSessions.getSession(sessionId);
        requestInfo.addHeader("Cookie", "JSESSIONID="+sessionId);
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
        new RequestHandler(mockSocket, dispatcherServlet, httpSessions).run();
        final String result = mockSocket.output();
        optionInfo.executeAfterOption(result);
        return result;
    }

    public static void setSessionId(String sessionId) {
        MockOption.sessionId = sessionId;
    }
}
