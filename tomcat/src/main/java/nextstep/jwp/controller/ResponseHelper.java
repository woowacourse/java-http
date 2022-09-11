package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.constant.HttpContent;
import org.apache.coyote.http11.constant.HttpHeader;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.response.HttpResponse;

public class ResponseHelper {

    private static final String RESOURCE_FOLDER = "static";

    public void loadResource(HttpResponse response, String url) throws IOException {

        URL resource = getClass().getClassLoader()
                .getResource(RESOURCE_FOLDER + url);
        String extension = url.substring(url.lastIndexOf(".") + 1);
        File targetFile = new File(resource.getFile());
        long fileSize = targetFile.length();

        response.statusCode(HttpStatus.OK);
        response.addHeader(HttpHeader.CONTENT_TYPE.value(), HttpContent.extensionToContentType(extension));
        response.addHeader(HttpHeader.CONTENT_LENGTH.value(), Long.toString(fileSize));
        response.body(new String(Files.readAllBytes(targetFile.toPath())));
    }

    public void loadRawString(HttpResponse response, String responseData) throws IOException {
        response.statusCode(HttpStatus.OK);
        response.addHeader(HttpHeader.CONTENT_TYPE.value(), HttpContent.HTML.getContentType());
        response.addHeader(HttpHeader.CONTENT_LENGTH.value(), String.valueOf(responseData.getBytes().length));
        response.body(responseData);
    }
}
