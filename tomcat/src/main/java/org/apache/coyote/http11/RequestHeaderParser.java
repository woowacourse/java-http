package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//요청 값을 모두 파싱해서 제공한다.
public class RequestHeaderParser {

    private static final Logger log = LoggerFactory.getLogger(RequestHeaderParser.class);

    public Map<String, String> parse(BufferedReader reader) {
        try {
            Map<String, String> request = new HashMap<>();

            String requestLine = reader.readLine();
            request.put("endpoint", requestLine.split(" ")[1]);

            reader.lines()
                    .takeWhile(line -> !line.isBlank())
                    .map(line -> line.split(":", 2))
                    .forEach(attribute ->
                            request.put(attribute[0].trim(), attribute[1].trim())
                    );

            return request;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public String getRequestPath(Map<String, String> requestHeaderInfos) {
        return removeQueryParamFromRequestEndPoint(
                requestHeaderInfos.get("endpoint")
        );
    }

    public Map<String, String> getQueryParams(Map<String, String> requestHeaderInfos) {
        String requestEndPoint = requestHeaderInfos.get("endpoint");
        int queryStringIndex = requestEndPoint.indexOf('?');
        String query = requestEndPoint.substring(queryStringIndex + 1);
        return parseQueryParams(query);
    }

    public String getAccept(Map<String, String> requestHeaderInfos) {
        return requestHeaderInfos.get("Accept");
    }

    private String removeQueryParamFromRequestEndPoint(String requestEndPoint) {
        int queryStringIndex = requestEndPoint.indexOf('?');

        if (queryStringIndex == -1) {
            return requestEndPoint;
        }

        return requestEndPoint.substring(0, queryStringIndex);
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();

        for (String pair : query.split("&")) {
            String[] keyAndValue = pair.split("=", 2);
            if (keyAndValue.length == 2) {
                queryParams.put(keyAndValue[0], keyAndValue[1]);
            }
        }

        return queryParams;
    }
}
