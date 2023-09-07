package org.apache.common;

public class HttpResponse {

    private String version;
    private StatusCode statusCode;
    private HttpHeader httpHeader;
    private String responseBody;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public void setHttpHeader(HttpHeader httpHeader) {
        this.httpHeader = httpHeader;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String serialize() {
        if (httpHeader != null) {
            String headers = "";
            for (String key : httpHeader.getHeaders().keySet()) {
                headers += key + ": " + httpHeader.getHeaders().get(key) + " \r\n";
            }
            return String.join("\r\n",
                    this.version + " "
                            + this.statusCode.getStatusCode() + " "
                            + this.statusCode.getStatusMessage() + " ",
                    headers,
                    this.responseBody);
        }
        return String.join("\r\n",
                this.version + " "
                        + this.statusCode.getStatusCode() + " "
                        + this.statusCode.getStatusMessage() + " ");
    }

    @Override
    public String toString() {
        return "HttpResponseNew{" +
                "version='" + version + '\'' +
                ", statusCode=" + statusCode +
                ", httpHeader=" + httpHeader +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}
