package nextstep.jwp.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {
    private static final String HTTP_METHOD = "HTTP METHOD";
    private static final String REQUEST_URL = "REQUEST URL";
    private static final String HTTP_VERSION = "HTTP VERSION";

    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);

    final BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public ServletFactory parse() throws IOException {
        final String requestFirstLine = bufferedReader.readLine();
        log.info("request First Line is = {}", requestFirstLine);
        final Map<String, String> firstLineAnalysis = parseFirstLine(requestFirstLine);
        final Map<String, String> requestQueryString = parseQueryString(firstLineAnalysis.get(REQUEST_URL));
        return ServletFactory.of(firstLineAnalysis.get(REQUEST_URL));
    }

    private Map<String, String> parseFirstLine(String requestFirstLine) {
        final int spaceAndSlashIndex = requestFirstLine.indexOf(" /");
        final int lastSpace = requestFirstLine.lastIndexOf(" ");

        Map<String, String> analysis = new HashMap<>();
        analysis.put(HTTP_METHOD, requestFirstLine.substring(0, spaceAndSlashIndex));
        analysis.put(REQUEST_URL, requestFirstLine.substring(spaceAndSlashIndex + 2, lastSpace));
        analysis.put(HTTP_VERSION, requestFirstLine.substring(lastSpace + 1));
        return analysis;
    }

    private Map<String, String> parseQueryString(String requestUrl) {
        final boolean isQueryString = requestUrl.contains("?");
        if (!isQueryString) {
            return null;
        }

        final Map<String, String> requestQueryString = new HashMap<>();
        final String queryString = requestUrl.substring(requestUrl.lastIndexOf("?") + 1);
        final String[] queries = queryString.split("&");
        for (String query : queries) {
            final String[] queriedValue = query.split("&");
            requestQueryString.put(queriedValue[0], queriedValue[1]);
        }
        return requestQueryString;
    }
}
