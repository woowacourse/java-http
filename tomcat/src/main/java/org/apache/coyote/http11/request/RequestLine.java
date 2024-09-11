package org.apache.coyote.http11.request;

import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.queryparam.QueryParams;
import org.apache.coyote.http11.version.HttpVersion;
import util.BiValue;
import util.StringUtil;

public class RequestLine {
    private static final String DELIMITER = " ";
    private static final String QUERY_PARAM_DELIMITER = "?";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int REQUEST_INDEX_SIZE = 3;

    private final HttpMethod httpMethod;
    private final Path path;
    private final QueryParams queryParams;
    private final HttpVersion version;

    public RequestLine(final HttpMethod httpMethod, final Path path, final QueryParams queryParams, final HttpVersion version) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.version = version;
    }

    public static RequestLine create(final String line) {
        final String[] requestLineInfos = line.split(DELIMITER);
        validateRequestLineFormat(requestLineInfos);
        final BiValue<Path, QueryParams> uriAndQueryParam = splitUriAndQueryParams(requestLineInfos[PATH_INDEX]);
        return new RequestLine(HttpMethod.from(requestLineInfos[HTTP_METHOD_INDEX]), uriAndQueryParam.first(), uriAndQueryParam.second(), HttpVersion.from(requestLineInfos[HTTP_VERSION_INDEX]));
    }
    private static void validateRequestLineFormat(final String[] requestLineInfos) {
        if(requestLineInfos.length !=REQUEST_INDEX_SIZE){
            throw new IllegalArgumentException("HTTP Request Line 문법이 잘못 됐습니다.");
        }
    }

    private static BiValue<Path, QueryParams> splitUriAndQueryParams(final String path) {
        final BiValue<String, String> biValue = StringUtil.splitBiValue(path, QUERY_PARAM_DELIMITER);
        return new BiValue<>(Path.from(biValue.first()), QueryParams.from(biValue.second()));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Path getPath() {
        return path;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "httpMethod=" + httpMethod +
                ", uri=" + path +
                ", version='" + version + '\'' +
                '}';
    }
}
