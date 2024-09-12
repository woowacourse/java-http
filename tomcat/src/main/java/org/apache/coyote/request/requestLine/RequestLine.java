package org.apache.coyote.request.requestLine;

import java.util.Objects;
import org.apache.coyote.protocolVersion.ProtocolVersion;
import org.apache.coyote.request.parser.RequestLineParser;

public class RequestLine {

    private final RequestMethod requestMethod;
    private final RequestPath requestPath;
    private final ProtocolVersion protocolVersion;

    public RequestLine(String requestLine) {
        RequestLineParser requestLineParser = new RequestLineParser(requestLine);

        this.requestMethod = RequestMethod.findMethod(requestLineParser.parseRequestMethod());
        this.requestPath = new RequestPath(requestLineParser.parseRequestPath());
        this.protocolVersion = ProtocolVersion.ofHTTP1();
    }

    public String getPathValue() {
        return requestPath.getPath();
    }

    public String getNoExtensionPath() {
        return requestPath.getNoExtensionPath();
    }

    public boolean isDefaultPath() {
        return requestPath.isDefaultPath();
    }

    public boolean isSameMethod(RequestMethod requestMethod) {
        return this.requestMethod.equals(requestMethod);
    }

    public boolean isSamePath(String controllerPath) {
        return requestPath.hasSamePath(controllerPath);
    }

    public boolean isResourcePath() {
        return requestPath.hasExtension();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestLine that = (RequestLine) o;
        return requestMethod == that.requestMethod && Objects.equals(requestPath, that.requestPath)
                && Objects.equals(protocolVersion, that.protocolVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestMethod, requestPath, protocolVersion);
    }
}
