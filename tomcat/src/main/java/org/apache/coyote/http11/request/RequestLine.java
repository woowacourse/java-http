package org.apache.coyote.http11.request;

import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.queryparam.QueryParams;
import util.BiValue;
import util.StringUtil;

public class RequestLine {
    private static final String DELIMITER = " ";
    private static final String QUERY_PARAM_DELIMITER = "?";

    private final HttpMethod httpMethod;
    private final Path path;
    private final QueryParams queryParams;
    private final String version;

    public RequestLine(final HttpMethod httpMethod, final Path path, final QueryParams queryParams, final String version) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.version = version;
    }

    public static RequestLine create(final String line) {
        final String[] arys = line.split(DELIMITER);
        final BiValue<Path, QueryParams> uriAndQueryParam = splitUriAndQueryParams(arys[1]);
        return new RequestLine(HttpMethod.from(arys[0]), uriAndQueryParam.first(), uriAndQueryParam.second(), arys[2]);
    }

    private static BiValue<Path, QueryParams> splitUriAndQueryParams(final String uri) {
        final BiValue<String, String> biValue = StringUtil.splitBiValue(uri, QUERY_PARAM_DELIMITER);
        return new BiValue<>(Path.from(biValue.first()), QueryParams.from(biValue.second()));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Path getPath() {
        return path;
    }

    public String getVersion() {
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
