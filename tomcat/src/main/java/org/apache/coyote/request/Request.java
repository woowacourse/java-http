package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Request {

    private final RequestMethod method;
    private final String path;
    private final String fileType;
    private final Map<String, String> queryParmap;
    private final Map<String, String> requestBody;

    private Request(
            final RequestMethod method,
            final String path,
            final String fileType,
            final Map<String, String> queryParmap,
            Map<String, String> requestBody
    ) {
        this.method = method;
        this.path = path;
        this.fileType = fileType;
        this.queryParmap = queryParmap;
        this.requestBody = requestBody;
    }

    public static Request from(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = makeReader(inputStream);
        String[] splitFirstLine = splitLine(bufferedReader.readLine());
        String pathFromStartLine = getPath(splitFirstLine);
        RequestMethod method = getMethod(splitFirstLine[0]);
        RequestMessage header = RequestMessage.from(bufferedReader);

        return getRequest(method, pathFromStartLine, header.getRequestBody());
    }

    private static BufferedReader makeReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private static String[] splitLine(String firstLine) {
        return firstLine.split(" ");
    }

    private static String getPath(String[] startLine) {
        return Arrays.stream(startLine)
                .filter(line -> line.startsWith("/"))
                .findFirst()
                .orElse("/");
    }

    private static RequestMethod getMethod(String method) {
        return RequestMethod.get(method);
    }

    private static Request getRequest(RequestMethod method, String path, Map<String, String> requestBody) {
        if (isStaticFile(path)) {
            final String fileType = getFileType(path);
            return new Request(method, path, fileType, new HashMap<>(), requestBody);
        }

        if (hasQuery(path)) {
            String query = getQueryLine(path);
            Map<String, String> queryFromLine = getQuery(query);
            path = findRealPath(path);
            return new Request(method, path, null, queryFromLine, requestBody);
        }

        return new Request(method, path, null, new HashMap<>(), requestBody);
    }

    private static boolean isStaticFile(String path) {
        return path.contains(".");
    }

    private static String getFileType(String path) {
        int extensionIndex = path.lastIndexOf(".");
        return path.substring(extensionIndex + 1);
    }

    private static boolean hasQuery(String path) {
        return path.contains("?") || path.contains("=");
    }

    private static String findRealPath(String path) {
        int endOfPath = path.indexOf("?");
        return path.substring(0, endOfPath);
    }

    private static String getQueryLine(String path) {
        int endOfPath = path.indexOf("?");
        return path.substring(endOfPath + 1);
    }

    private static Map<String, String> getQuery(String query) {
        Map<String, String> queryMap = new HashMap<>();
        getKeyAndValue(query, queryMap, "=");
        return queryMap;
    }

    private static void getKeyAndValue(String query, Map<String, String> map, String regex) {
        Arrays.stream(query.split("&"))
                .forEach(splitQuery -> map.put(findKey(splitQuery, regex), findValue(splitQuery, regex)));
    }

    private static String findKey(String splitQuery, String regex) {
        return splitQuery.substring(0, splitQuery.indexOf(regex));
    }

    private static String findValue(String splitQuery, String regex) {
        return splitQuery.substring(splitQuery.indexOf(regex) + 1);
    }

    public boolean isFile() {
        return Objects.nonNull(this.fileType);
    }

    public boolean hasQueryString() {
        return !this.queryParmap.isEmpty();
    }

    public Map<String, String> getQueryParaMap() {
        return queryParmap;
    }

    public String getFileType() {
        if (isFile()) {
            return fileType;
        }
        return "";
    }

    public String getPath() {
        return path;
    }

    public boolean isPost() {
        return this.method.isPost();
    }

    public boolean isGet() {
        return this.method.isGet();
    }

    public Map<String, String> getRequestBody() {
        return this.requestBody;
    }
}
