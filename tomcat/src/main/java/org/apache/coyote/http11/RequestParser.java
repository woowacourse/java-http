package org.apache.coyote.http11;

import nextstep.jwp.vo.FileName;
import nextstep.jwp.vo.FormData;
import nextstep.jwp.vo.HttpCookie;

import java.util.*;

public class RequestParser {

    private static final String DEFAULT_FILE_NAME = "Hello world!";
    private static final String DEFAULT_EXTENSION = "html";
    private static final String BLANK_DELIMITER = " ";
    private static final String EACH_QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_STRING_DELIMITER = "\\?";
    private static final String ROOT_DIR = "/";
    private static final String EXTENSION_DELIMITER = "\\.";
    private static final String COOKIE = "Cookie";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String QUERY_STRING_PAIR_DELIMITER = "=";

    private static final int QUERY_STRING_KEY_INDEX = 0;
    private static final int QUERY_STRING_VALUE_INDEX = 1;
    private static final int SPLIT_SIZE = 2;

    private static final int FIRST_INDEX = 0;

    private static final int FILE_NAME_INDEX = 1;

    private static final int BASE_NAME_INDEX = 0;

    private static final int EXTENSION_INDEX = 1;

    private final List<String> rawRequest;

    public RequestParser(List<String> rawRequest) {
        this.rawRequest = rawRequest;
    }

    public FormData generateQueryStrings() {
        String[] rawQuery = getParsedFirstRequestLine()[FILE_NAME_INDEX].split(QUERY_STRING_DELIMITER);
        if (rawQuery.length != SPLIT_SIZE) {
            return new FormData(Collections.emptyMap());
        }
        String[] parsedQuery = rawQuery[1].split(EACH_QUERY_STRING_DELIMITER);
        return new FormData(parseData(parsedQuery));
    }

    private String[] getParsedFirstRequestLine() {
        return rawRequest.get(FIRST_INDEX).split(BLANK_DELIMITER);
    }

    public FileName generateFileName() {
        String[] parsedRawRequest = getParsedFirstRequestLine();
        if (parsedRawRequest.length < SPLIT_SIZE) {
            return new FileName(DEFAULT_FILE_NAME, DEFAULT_EXTENSION);
        }
        String rawFileName = parsedRawRequest[FILE_NAME_INDEX];
        if (rawFileName.equals(ROOT_DIR)) {
            return new FileName(DEFAULT_FILE_NAME, DEFAULT_EXTENSION);
        }
        FormData formData = this.generateQueryStrings();
        if (!formData.isEmpty()) {
            int queryStringIndex = rawFileName.indexOf("?");
            rawFileName = rawFileName.substring(FIRST_INDEX, queryStringIndex);
        }
        String[] parsedByExtension = rawFileName.split(EXTENSION_DELIMITER);
        if (parsedByExtension.length > SPLIT_SIZE) {
            return new FileName(DEFAULT_FILE_NAME, DEFAULT_EXTENSION);
        }
        if (parsedByExtension.length == 1) {
            return new FileName(parsedByExtension[BASE_NAME_INDEX], DEFAULT_EXTENSION);
        }
        return new FileName(parsedByExtension[BASE_NAME_INDEX],parsedByExtension[EXTENSION_INDEX]);
    }

    public FormData generateRequestBody() {
        String[] parsedQuery = rawRequest.get(rawRequest.size() - 1).split(EACH_QUERY_STRING_DELIMITER);
        return new FormData(parseData(parsedQuery));
    }

    public String generateMethod() {
        return getParsedFirstRequestLine()[FIRST_INDEX];
    }

    public HttpCookie generateCookie() {
        boolean isCookieExists = rawRequest.stream()
                .anyMatch(each -> each.startsWith(COOKIE));
        if (!isCookieExists) {
            return new HttpCookie(Collections.emptyMap());
        }
        String[] rawCookie = rawRequest.stream()
                .filter(each -> each.startsWith(COOKIE))
                .findFirst()
                .orElseGet(() -> "")
                .replace(COOKIE + ": ", "")
                .split(COOKIE_DELIMITER);

        return new HttpCookie(parseData(rawCookie));
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
