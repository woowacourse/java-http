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

    private final List<String> rawRequest;

    public RequestParser(List<String> rawRequest) {
        this.rawRequest = rawRequest;
    }

    public FormData generateQueryStrings() {
        String[] rawQuery = rawRequest.get(0).split(BLANK_DELIMITER)[1].split(QUERY_STRING_DELIMITER);
        if (rawQuery.length != 2) {
            return new FormData(Collections.emptyMap());
        }
        String[] parsedQuery = rawQuery[1].split(EACH_QUERY_STRING_DELIMITER);
        return parseData(parsedQuery);
    }

    public FileName generateFileName() {
        String[] parsedRawRequest = rawRequest.get(0).split(BLANK_DELIMITER);
        if (parsedRawRequest.length < 2) {
            return new FileName(DEFAULT_FILE_NAME, DEFAULT_EXTENSION);
        }
        String rawFileName = parsedRawRequest[1];
        if (rawFileName.equals(ROOT_DIR)) {
            return new FileName(DEFAULT_FILE_NAME, DEFAULT_EXTENSION);
        }
        FormData formData = this.generateQueryStrings();
        if (!formData.isEmpty()) {
            int queryStringIndex = rawFileName.indexOf("?");
            rawFileName = rawFileName.substring(0, queryStringIndex);
        }
        String[] parsedByExtension = rawFileName.split(EXTENSION_DELIMITER);
        if (parsedByExtension.length > 2) {
            return new FileName(DEFAULT_FILE_NAME, DEFAULT_EXTENSION);
        }
        if (parsedByExtension.length == 1) {
            return new FileName(parsedByExtension[0], DEFAULT_EXTENSION);
        }
        return new FileName(parsedByExtension[0],parsedByExtension[1]);
    }

    public FormData generateRequestBody() {
        String[] parsedQuery = rawRequest.get(rawRequest.size() - 1).split(EACH_QUERY_STRING_DELIMITER);
        return parseData(parsedQuery);
    }

    public String generateMethod() {
        return this.rawRequest.get(0).split(BLANK_DELIMITER)[0];
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

        Map<String, String> resultCookies = new HashMap<>();
        for (String eachCookie : rawCookie) {
            String[] parsedCookie = eachCookie.split(QUERY_STRING_PAIR_DELIMITER);
            resultCookies.put(parsedCookie[0], parsedCookie[1]);
        }
        return new HttpCookie(resultCookies);
    }

    private FormData parseData(String[] raw) {
        Map<String, String> queryStringMap = new HashMap<>();
        boolean isNotValidPair = Arrays.stream(raw)
                .anyMatch(eachQuery -> eachQuery.split(QUERY_STRING_PAIR_DELIMITER).length != 2);
        if (isNotValidPair) {
            return new FormData(Collections.emptyMap());
        }
        for (String eachQuery : raw) {
            String[] parsedEntry = eachQuery.split(QUERY_STRING_PAIR_DELIMITER);
            queryStringMap.put(parsedEntry[0], parsedEntry[1]);
        }
        return new FormData(queryStringMap);
    }
}
