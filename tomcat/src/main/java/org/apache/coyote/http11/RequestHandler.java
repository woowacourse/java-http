package org.apache.coyote.http11;

public class RequestHandler {

    private String httpStatus;
    private String responseFilePath;

    private RequestHandler(String httpStatus, String responseFilePath) {
        this.httpStatus = httpStatus;
        this.responseFilePath = responseFilePath;
    }

    public static RequestHandler of(String httpStatus, String fileName) {
        String filePath = RequestHandler.class.getClassLoader().getResource(fileName).getPath();
        return new RequestHandler(httpStatus, filePath);
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public String getResponseFilePath() {
        return responseFilePath;
    }
}
