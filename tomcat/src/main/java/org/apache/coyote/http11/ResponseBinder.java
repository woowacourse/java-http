package org.apache.coyote.http11;

class ResponseBinder {

    String buildSuccessfulResponse(String responseBody) {
        return this.buildSuccessfulResponse("text/html", responseBody);
    }

    String buildSuccessfulResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    String buildFailedResponse(int statusCode, String statusMessage, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " " + statusMessage,
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
