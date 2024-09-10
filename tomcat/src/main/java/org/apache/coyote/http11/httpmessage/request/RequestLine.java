package org.apache.coyote.http11.httpmessage.request;

public record RequestLine (
        Method method,
        String target,
        String httpVersion
) {

    public static RequestLine parseFrom(String requestLineText) {
        String[] token = requestLineText.split(" ");
        return new RequestLine(Method.findByName(token[0]), token[1], token[2]);
    }

    public boolean isPost() {
        return method == Method.POST;
    }

    public boolean isStaticResourceRequest() {
        String[] pathToken = target.split("/");
        String extension = pathToken[pathToken.length - 1];
        return extension.contains(".css") ||
                extension.contains(".html") ||
                extension.contains(".js");
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method=" + method +
                ", target='" + target + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                '}';
    }
}
