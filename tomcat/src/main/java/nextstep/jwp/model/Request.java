package nextstep.jwp.model;

import nextstep.jwp.exception.InvalidRequest;
import nextstep.jwp.vo.FileName;
import nextstep.jwp.vo.FormData;
import nextstep.jwp.vo.HttpCookie;
import nextstep.jwp.vo.RequestMethod;

import java.util.*;

public class Request {
    private static final String HEADER_DELIMITER = ": ";
    private static final String DEFAULT_FILE_NAME = "Hello world!";
    private static final String DEFAULT_EXTENSION = "html";
    private static final String EMPTY_VALUE = "";
    private static final String EACH_QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_STRING_DELIMITER = "\\?";
    private static final String ROOT_DIR = "/";
    private static final String EXTENSION_DELIMITER = "\\.";
    private static final String COOKIE = "Cookie";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String QUERY_STRING_PAIR_DELIMITER = "=";
    private static final String BODY = "Body";
    private static final String BLANK_DELIMITER = " ";
    private static final String METHOD = "Method";
    private static final String URL = "Url";
    private static final String QUERY = "Query";
    private static final String EMPTY_REQUEST = "Request는 최소 1줄 이상이여야 합니다.";
    private static final String INVALID_FIRST_LINE = "Request의 첫 줄이 올바르지 않습니다.";
    private static final String INVALID_PAIR_SIZE = "헤더의 쌍이 맞지 않습니다.";
    private static final String QUERY_STRING_VALUE = "?";
    private static final int QUERY_STRING_KEY_INDEX = 0;
    private static final int QUERY_STRING_VALUE_INDEX = 1;
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final int SPLIT_SIZE = 2;

    private final Map<String, String> request;

    private Request(Map<String, String> request) {
        this.request = request;
    }

    public static Request of(List<String> rawRequest, String body) {
        validateRawRequest(rawRequest);
        Map<String, String> request = new HashMap<>();
        parsedBody(body, request);
        parseFirstLine(rawRequest, request);
        parseHeaders(rawRequest, request);

        return new Request(request);
    }

    private static void parsedBody(String body, Map<String, String> request) {
        if (!body.isEmpty()) {
            request.put(BODY, body);
        }
    }

    private static void parseHeaders(List<String> rawRequest, Map<String, String> request) {
        rawRequest.subList(1, rawRequest.size())
                .stream()
                .map(header -> header.split(HEADER_DELIMITER))
                .forEach(parsedHeader -> request.put(parsedHeader[HEADER_KEY_INDEX], parsedHeader[HEADER_VALUE_INDEX]));
    }

    private static void parseFirstLine(List<String> rawRequest, Map<String, String> request) {
        String[] firstLine = rawRequest.get(0).split(BLANK_DELIMITER);
        request.put(METHOD, firstLine[0]);
        request.put(URL, firstLine[1]);
        String url = request.get(URL);
        if (url.contains(QUERY_STRING_VALUE)) {
            String[] splitByQuery = url.split(QUERY_STRING_DELIMITER);
            request.put(URL, splitByQuery[0]);
            request.put(QUERY, splitByQuery[1]);
        }
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

    public RequestMethod getRequestMethod() {
        if (this.request.get(METHOD).equals(RequestMethod.GET.name())) {
            return RequestMethod.GET;
        }
        return RequestMethod.POST;
    }

    public FileName getFileName() {
        String fileName = this.request.get(URL);
        if (fileName.equals(ROOT_DIR)) {
            return new FileName(DEFAULT_FILE_NAME, DEFAULT_EXTENSION);
        }
        String[] parsedFileName = fileName.split(EXTENSION_DELIMITER);
        if (parsedFileName.length < SPLIT_SIZE) {
            return new FileName(parsedFileName[0], DEFAULT_EXTENSION);
        }
        return new FileName(parsedFileName[0], parsedFileName[1]);
    }

    public HttpCookie getCookie() {
        return new HttpCookie(parseData(
                request.getOrDefault(COOKIE, EMPTY_VALUE).split(COOKIE_DELIMITER)));
    }

    public FormData getQueryString() {
        return new FormData(parseData(
                request.getOrDefault(QUERY, EMPTY_VALUE).split(EACH_QUERY_STRING_DELIMITER)));
    }

    public FormData getBody() {
        return new FormData(parseData(
                request.getOrDefault(BODY, EMPTY_VALUE).split(EACH_QUERY_STRING_DELIMITER)));
    }

    private Map<String, String> parseData(String[] raw) {
        Map<String, String> queryStringMap = new HashMap<>();
        boolean isNotValidPair = Arrays.stream(raw)
                .anyMatch(eachQuery ->
                        eachQuery.split(QUERY_STRING_PAIR_DELIMITER).length != SPLIT_SIZE);
        if (isNotValidPair) {
            return Collections.emptyMap();
        }
        for (String eachQuery : raw) {
            String[] parsedEntry = eachQuery.split(QUERY_STRING_PAIR_DELIMITER);
            queryStringMap.put(parsedEntry[QUERY_STRING_KEY_INDEX], parsedEntry[QUERY_STRING_VALUE_INDEX]);
        }
        return queryStringMap;
    }
}
