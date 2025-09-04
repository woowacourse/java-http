package org.apache.coyote.http11.response;

public record HttpResponse() {

    public byte[] getBytes() {
        final String responseBody = "Hello world!";

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
        return response.getBytes();
    }
}
