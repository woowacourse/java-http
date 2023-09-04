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

    private final String path;
    private final String fileType;
    private final Map<String, String> queryParmap;

    private Request(
            final String path,
            final String fileType,
            final Map<String, String> queryParmap
    ) {
        this.path = path;
        this.fileType = fileType;
        this.queryParmap = queryParmap;
    }

    public static Request from(InputStream inputStream) throws IOException {
        String pathFromStartLine = readFirstLine(inputStream);
        return getRequest(pathFromStartLine);
    }

    private static String readFirstLine(InputStream inputStream) throws IOException {
        final BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
        String[] startLine = inputReader.readLine().split(" ");
        return getPath(startLine);
    }

    private static String getPath(String[] startLine) {
        return Arrays.stream(startLine)
                .filter(line -> line.startsWith("/"))
                .findFirst()
                .orElse("/");
    }

    private static Request getRequest(String path) {
        if(isStaticFile(path)){
            final String fileType = getFileType(path);
            return new Request(path, fileType, new HashMap<>());
        }

        if (hasQuery(path)) {
            String query = getQueryLine(path);
            Map<String, String> queryFromLine = getQuery(query);
            path = findRealPath(path);
            return new Request(path, null, queryFromLine);
        }

        return new Request(path, null, new HashMap<>());
    }

    private static boolean isStaticFile(String pathFromStartLine) {
        return pathFromStartLine.contains(".");
    }

    private static String getFileType(String path) {
        int extensionIndex = path.lastIndexOf(".");
        return path.substring(extensionIndex + 1);
    }

    private static boolean hasQuery(String pathFromStartLine) {
        return pathFromStartLine.contains("?");
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

        Arrays.stream(query.split("&"))
                .forEach(splitQuery -> queryMap.put(findKey(splitQuery), findValue(splitQuery)));
        return queryMap;
    }

    private static String findKey(String splitQuery) {
        return splitQuery.substring(0, splitQuery.indexOf("="));
    }

    private static String findValue(String splitQuery) {
        return splitQuery.substring(splitQuery.indexOf("=") + 1);
    }

    public boolean isFile(){
        return Objects.nonNull(this.fileType);
    }

    public boolean hasQueryString(){
        return !this.queryParmap.isEmpty();
    }

    public Map<String, String> getQueryParaMap() {
        return queryParmap;
    }

    public String getFileType() {
        if(isFile()){
            return fileType;
        }
        return "";
    }

    public String getPath() {
        return path;
    }
}
