package org.apache.catalina.request;

import org.apache.catalina.Mapper;

import java.util.List;
import java.util.Map;

public class RequestCreator {

    private static final String headerDelimiter = ": ";

    public HttpRequest create(List<String> rawHeader, String rawPayload) {
        String method = getMethod(rawHeader);
        String path = getPath(rawHeader);
        Map<String, String> header = Mapper.toMap(rawHeader, headerDelimiter);
        Map<String, String> payload = Mapper.toMap(rawPayload);
        return new HttpRequest(method, path, header, payload);
    }

    private String getMethod(List<String> header) {
        String firstLine = header.get(0);
        return firstLine.split(" ")[0];
    }

    private String getPath(List<String> header) {
        String firstLine = header.get(0);
        return firstLine.split(" ")[1];
    }
}
