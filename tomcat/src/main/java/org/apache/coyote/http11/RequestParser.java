package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class RequestParser {

    private final List<String> rawRequest;

    public RequestParser(List<String> rawRequest) {
        this.rawRequest = rawRequest;
    }

    public FormData generateQueryStrings() {
        String[] rawQuery = rawRequest.get(0).split(" ")[1].split("\\?");
        if (rawQuery.length != 2) {
            return new FormData(Collections.emptyMap());
        }
        String[] parsedQuery = rawQuery[1].split("&");
        Map<String, String> queryStringMap = new HashMap<>();
        for (String eachQuery : parsedQuery) {
            String[] parsedEntry = eachQuery.split("=");
            if (parsedEntry.length != 2) {
                return new FormData(Collections.emptyMap());
            }
            queryStringMap.put(parsedEntry[0], parsedEntry[1]);
        }
        return new FormData(queryStringMap);
    }

    public FileName generateFileName() {
        String[] parsedRawRequest = rawRequest.get(0).split(" ");
        if (parsedRawRequest.length < 2) {
            return new FileName("Hello world!", "html");
        }
        String rawFileName = parsedRawRequest[1];
        if (rawFileName.equals("/")) {
            return new FileName("Hello world!", "html");
        }
        FormData formData = this.generateQueryStrings();
        if (!formData.isEmpty()) {
            int queryStringIndex = rawFileName.indexOf("?");
            rawFileName = rawFileName.substring(0, queryStringIndex);
        }
        String[] parsedByExtension = rawFileName.split("\\.");
        if (parsedByExtension.length > 2) {
            return new FileName("Hello world!", "html");
        }
        if (parsedByExtension.length == 1) {
            return new FileName(parsedByExtension[0], "html");
        }
        return new FileName(parsedByExtension[0],parsedByExtension[1]);
    }

    public FormData generateRequestBody() {
        String[] parsedQuery = rawRequest.get(rawRequest.size() - 1).split("&");
        Map<String, String> queryStringMap = new HashMap<>();
        for (String eachQuery : parsedQuery) {
            String[] parsedEntry = eachQuery.split("=");
            if (parsedEntry.length != 2) {
                return new FormData(Collections.emptyMap());
            }
            queryStringMap.put(parsedEntry[0], parsedEntry[1]);
        }
        return new FormData(queryStringMap);
    }

    public String generateMethod() {
        System.out.println(this.rawRequest.get(0));
        return this.rawRequest.get(0).split(" ")[0];
    }

    public HttpCookie generateCookie() {
        boolean isCookieExists = rawRequest.stream()
                .anyMatch(each -> each.startsWith("Cookie"));
        if (!isCookieExists) {
            return new HttpCookie(Collections.emptyMap());
        }
        String[] rawCookie = rawRequest.stream()
                .filter(each -> each.startsWith("Cookie"))
                .findFirst()
                .orElseGet(() -> "")
                .replace("Cookie: ", "")
                .split("; ");

        Map<String, String> resultCookies = new HashMap<>();
        for (String eachCookie : rawCookie) {
            String[] parsedCookie = eachCookie.split("=");
            resultCookies.put(parsedCookie[0], parsedCookie[1]);
        }
        return new HttpCookie(resultCookies);
    }
}
