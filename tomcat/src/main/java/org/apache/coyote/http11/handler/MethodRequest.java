package org.apache.coyote.http11.handler;

public class MethodRequest {
    private final String endPoint;
    private final QueryParameters queryParams;

    public MethodRequest(String url) {
        String[] targetToken = url.split("\\?");
        this.endPoint = targetToken[0];

        if (targetToken.length == 1) {
            this.queryParams = QueryParameters.empty();
        } else {
            this.queryParams = QueryParameters.parseFrom(targetToken[1]);
        }
    }

    public String getParam(String key) {
        return queryParams.getParam(key);
    }

    public String getEndPoint() {
        return this.endPoint;
    }
}
