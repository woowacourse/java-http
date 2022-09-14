package nextstep.jwp.vo;

import java.util.List;

public enum RequestMethod {

    GET,
    POST;

    private static final String BLANK_DELIMITER = " ";
    public static RequestMethod from(List<String> rawRequest) {
        if (rawRequest.isEmpty()) {
            throw new IllegalArgumentException("Request는 최소 한 줄 이상이어야 합니다.");
        }
        String[] firstLine = rawRequest.get(0).split(BLANK_DELIMITER);
        if (firstLine.length == 0) {
            throw new IllegalArgumentException("HTTP Method를 파싱할 수 없습니다.");
        }
        String method = firstLine[0];
        if (method.equals(GET.name())) {
            return GET;
        }
        return POST;
    }
}
