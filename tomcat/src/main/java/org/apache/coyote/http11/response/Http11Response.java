package org.apache.coyote.http11.response;

public class Http11Response {

    private final String responseBody;
    private final Http11ResponseHeaders headers;
    private final String firstLine = "HTTP/1.1 200 OK ";

    public Http11Response(String responseBody, String fileExtensions) {
        this.responseBody = responseBody;
        this.headers = Http11ResponseHeaders.from(String.join("\r\n",
                "Content-Type: text/" + fileExtensions + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " "));
    }

    public byte[] getBytes() {
        return String.join("\r\n",
                firstLine,
                headers.asString(),
                responseBody).getBytes();
    }
}
