package nextstep.jwp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HeaderLine {

    private List<String> headerLines;

    public HeaderLine(List<String> headerLines) {
        this.headerLines = headerLines;
    }

    public static HeaderLine readFromInputStream(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> headerLine = new LinkedList<>();
        while (reader.ready()) {
            headerLine.add(reader.readLine());
        }
        return new HeaderLine(headerLine);
    }

    public boolean isOnQuery() {
        final String firstLine = headerLines.get(0);
        return firstLine.contains("?");
    }

    public String getRequestURLWithoutQuery() {
        final String firstLine = headerLines.get(0);
        final String[] splitFirstLine = firstLine.split(" ");
        final String rawURI = splitFirstLine[1];
        final int index = rawURI.indexOf("?");
        return rawURI.substring(0,index);
    }

    public String getRequestURL() {
        final String firstLine = headerLines.get(0);
        final String[] splitFirstLine = firstLine.split(" ");
        return splitFirstLine[1];
    }

    public Map<String, String> queryOnURI() {
        final String uri = getRequestURL();
        final int index = uri.indexOf("?");
        final String queryString = uri.substring(index + 1);
        final Map<String, String> queries = new HashMap<>();
        final String[] queryUnits = queryString.split("&");

        for (String queryUnit : queryUnits) {
            final String[] queryComposition = queryUnit.split("=");
            queries.put(queryComposition[0], queryComposition[1]);
        }
        return queries;
    }
}
