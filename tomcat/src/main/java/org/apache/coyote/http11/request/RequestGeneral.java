package org.apache.coyote.http11.request;

import java.util.List;
import org.apache.coyote.http11.message.HttpVersion;
import org.apache.exception.TempException;
import org.apache.util.StringUtil;

public class RequestGeneral {

    private final RequestMethod method;
    private final RequestPath path;
    private final HttpVersion httpVersion;

    private RequestGeneral(RequestMethod method, RequestPath path, HttpVersion httpVersion) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static RequestGeneral parse(String startLine) {
        List<String> generalStrings = splitAndValidate(startLine);
        RequestMethod requestMethod = RequestMethod.findMethod(generalStrings.get(0));
        RequestPath requestPath = RequestPath.parse(generalStrings.get(1));
        HttpVersion httpVersion = HttpVersion.findVersion(generalStrings.get(2));
        return new RequestGeneral(requestMethod, requestPath, httpVersion);
    }

    private static List<String> splitAndValidate(String startLine) {
        List<String> generalStrings = StringUtil.splitAsList(startLine, " ");
        validateSize(generalStrings);
        return generalStrings;
    }

    private static void validateSize(List<String> generalStrings) {
        if (generalStrings.size() != 3) {
            throw new TempException();
        }
    }

    public RequestMethod getMethod() {
        return method;
    }

    public RequestPath getPath() {
        return path;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    @Override
    public String toString() {
        return "RequestGeneral{" +
                "method=" + method +
                ", path=" + path +
                ", httpVersion=" + httpVersion +
                '}';
    }
}
