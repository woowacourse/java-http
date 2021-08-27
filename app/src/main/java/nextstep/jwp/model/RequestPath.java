package nextstep.jwp.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestPath {

    private final String path;

    public RequestPath(String path) {
        this.path = path;
    }

    public Map<String, String> queries() throws IOException {
        int index = findIndex('?');
        String[] querySlices = path.substring(index + 1).split("&");
        Map<String, String> queries = new HashMap<>();
        for (String querySlice : querySlices) {
            String[] query = querySlice.split("=");
            queries.put(query[0], query[1]);
        }
        return queries;
    }

    public String path() {
        return this.path;
    }

    private int findIndex(char symbol) throws IOException {
        int index = path.indexOf(symbol);
        if (index == -1) {
            throw new IOException();
        }
        return index;
    }

    public boolean hasQueryString() {
        return path.contains("?");
    }
}
