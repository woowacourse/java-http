package nextstep.mockweb.result;

import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.response.StatusCode;

public class MockResult {

    private static final int STATUS_ORDER = 0;
    private static final int HEADER_ORDER = 1;
    private static final int BODY_ORDER = 2;
    private static final String ENTER = "\r\n";

    private StatusLine statusLine;
    private ResponseHeader responseHeader;
    private ResponseBody responseBody;


    public MockResult(String result) {
        this.statusLine = new StatusLine();
        this.responseHeader = new ResponseHeader();
        this.responseBody = new ResponseBody();
        int lineCount = 0;
        final String[] response = result.split(ENTER);
        for (int i = 0; i < response.length; i++) {
            if(lineCount == STATUS_ORDER) {
                statusLine.addStatusLine(response[i]);
                lineCount++;
                continue;
            }

            if(lineCount == HEADER_ORDER) {
                if (response[i].equals("")) {
                    lineCount++;
                    i++;
                } else {
                    responseHeader.addHeader(response[i]);
                }
                continue;
            }

            if(lineCount == BODY_ORDER) {
                responseBody.addBodyByLine(response[i]);
            }
        }
    }

    public String asString() {
        return "result";
    }

    public StatusCode statusCode() {
        return statusLine.statusCode();
    }

    public String headerValue(String key) {
        return responseHeader.get(key);
    }

    public String body() {
        return responseBody.get();
    }
}
