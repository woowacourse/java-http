package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class RequestStartLine {

    private Method method;
    private String path;
    private HashMap<String, String> queryString;
    private String versionOfProtocol;

    public RequestStartLine(BufferedReader bufferedReader) throws IOException {
        String[] splitLine = bufferedReader.readLine().split(" ");

        this.method = Method.valueOf(splitLine[0]);
        this.path = splitLine[1];
        this.versionOfProtocol = splitLine[2];
        this.queryString = new HashMap<>();

        if (path.contains("?")) {
            String uri = splitLine[1];
            int index = uri.indexOf("?");
            this.path = uri.substring(0, index);
            this.queryString = queryString(uri.substring(index + 1));
        }
    }

    private HashMap<String, String> queryString(String uri) {
        HashMap<String, String> hashMap = new HashMap<>();

        String[] queries = uri.split("&");

        for (String query : queries) {
            String[] keyValue = query.split("=");
            hashMap.put(keyValue[0], keyValue[1]);
        }

        return hashMap;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getAttribute(String name) {
        return queryString.get(name);
    }

    public String getVersionOfProtocol() {
        return versionOfProtocol;
    }
}
