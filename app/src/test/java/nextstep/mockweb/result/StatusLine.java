package nextstep.mockweb.result;

import nextstep.jwp.webserver.response.StatusCode;

public class StatusLine {

    private static final String LINE_SPLIT_REGEX = " ";
    private static final int CODE_NUMBER_INDEX = 1;
    private static final int CODE_STRING_INDEX = 2;

    private StatusCode statusCode;

    public void addStatusLine(String line) {
        if(line == null || line.isEmpty()) return;
        final String[] statusLine = line.split(LINE_SPLIT_REGEX);
        this.statusCode =
                StatusCode.findByNumber(Integer.parseInt(statusLine[CODE_NUMBER_INDEX]));
    }

    public StatusCode statusCode() {
        return statusCode;
    }
}
