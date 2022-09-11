package nextstep.jwp.model;

import nextstep.jwp.exception.InvalidRequest;
import nextstep.jwp.vo.HttpCookie;
import nextstep.jwp.vo.RequestBody;
import nextstep.jwp.vo.RequestHeaders;
import nextstep.jwp.vo.RequestMethod;

import java.util.List;

public class Request {
    private static final String HEADER_DELIMITER = ": ";
    private static final String BLANK_DELIMITER = " ";
    private static final String EMPTY_REQUEST = "Request는 최소 1줄 이상이여야 합니다.";
    private static final String INVALID_FIRST_LINE = "Request의 첫 줄이 올바르지 않습니다.";
    private static final String INVALID_PAIR_SIZE = "헤더의 쌍이 맞지 않습니다.";
    private static final int SPLIT_SIZE = 2;

    private final FileName fileName;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;
    private final RequestMethod requestMethod;

    public Request(FileName fileName, RequestHeaders requestHeaders,
                   RequestBody requestBody, RequestMethod requestMethod) {
        this.fileName = fileName;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.requestMethod = requestMethod;
    }

    public static Request of(List<String> rawRequest, String body) {
        validateRawRequest(rawRequest);
        FileName fileName = FileName.from(rawRequest);
        RequestMethod requestMethod = RequestMethod.from(rawRequest);
        RequestHeaders requestHeaders = RequestHeaders.from(rawRequest);
        RequestBody requestBody = RequestBody.from(body);
        return new Request(fileName, requestHeaders, requestBody, requestMethod);
    }

    private static void validateRawRequest(List<String> rawRequest) {
        if (rawRequest.isEmpty()) {
            throw new InvalidRequest(EMPTY_REQUEST);
        }
        if (rawRequest.get(0).split(BLANK_DELIMITER).length < SPLIT_SIZE) {
            throw new InvalidRequest(INVALID_FIRST_LINE);
        }
        boolean isInvalidPairSize = rawRequest.subList(1, rawRequest.size())
                .stream()
                .anyMatch(header -> header.split(HEADER_DELIMITER).length != SPLIT_SIZE);
        if (isInvalidPairSize) {
            throw new InvalidRequest(INVALID_PAIR_SIZE);
        }
    }

    public boolean canMapped(RequestMethod method, String url) {
        return this.fileName.isSame(url) && this.requestMethod == method;
    }

    public FileName getFileName() {
        return this.fileName;
    }

    public FormData getBody() {
        return this.requestBody.getBodies();
    }

    public HttpCookie getCookie() {
        return this.requestHeaders.generateCookie();
    }
}
