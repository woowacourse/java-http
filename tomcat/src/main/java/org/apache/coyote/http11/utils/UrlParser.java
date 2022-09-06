package org.apache.coyote.http11.utils;

import static org.apache.coyote.http11.response.ContentType.*;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.dto.JoinQueryDto;
import org.apache.coyote.http11.dto.LoginQueryDataDto;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlParser {
    private static final Logger log = LoggerFactory.getLogger(UrlParser.class);
    private static final String PATH_STANDARD = "?";
    private static final String REQUEST_STANDARD = "&";
    private static final String DATA_STANDARD = "=";
    private static final int VALUE_INDEX = 1;

    public static LoginQueryDataDto loginQuery(String query) {

        String[] dataMap = query.split(REQUEST_STANDARD);
        String account = getValue(dataMap, 0);
        String password = getValue(dataMap, 1);

        return new LoginQueryDataDto(account, password);
    }

    private static String getValue(String[] dataMap, int index) {
        return dataMap[index].split(DATA_STANDARD)[VALUE_INDEX];
    }

    public static HttpMethod extractMethod(final String httpRequest) {
        String method = httpRequest.split(" ")[0];
        return HttpMethod.valueOf(method);
    }

    public static String extractUri(final String httpRequest) {
        return httpRequest.split(" ")[1];
    }

    public static JoinQueryDto joinQuery(String requestBody) {
        String[] dataMap = requestBody.split(REQUEST_STANDARD);
        return new JoinQueryDto(getValue(dataMap, 0), getValue(dataMap, 1), getValue(dataMap, 2));
    }

    public static String convertEmptyToHtml(String url){
        String resource = url;
        int index = url.indexOf(".");
        if (index == -1) {
            resource = url + "." + TEXT_HTML.getExtension();
        }
        return resource;
    }
}
