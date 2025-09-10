package com.techcourse.http.response;

import com.techcourse.http.request.HttpRequest;
import com.techcourse.util.FileUtil;

public record ResponseBody(
        String value
) {

    public static ResponseBody empty() {
        return new ResponseBody("");
    }

    public static ResponseBody helloWorld() {
        return new ResponseBody("Hello world!");
    }

    public static ResponseBody createBy(final HttpRequest httpRequest) {
        String fileName = FileUtil.createFileName(httpRequest.getFilePath());

        String responseBodyValue = FileUtil.readResource(fileName);
        return new ResponseBody(responseBodyValue);
    }
}
