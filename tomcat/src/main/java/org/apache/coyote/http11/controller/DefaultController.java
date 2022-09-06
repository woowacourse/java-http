package org.apache.coyote.http11.controller;

import nextstep.jwp.model.Request;
import nextstep.jwp.vo.FileName;
import nextstep.jwp.vo.Response;
import nextstep.jwp.vo.ResponseStatus;
import org.apache.coyote.http11.HtmlLoader;

import java.io.IOException;

import static nextstep.jwp.vo.HttpHeader.*;

public class DefaultController implements Controller {

    private static final String PREFIX = "static/";

    private static final DefaultController INSTANCE = new DefaultController();

    @Override
    public Response respond(Request request) throws IOException {
        FileName fileName = request.getFileName();
        String responseBody = HtmlLoader.generateFile(PREFIX + fileName.concat());

        return Response.from(ResponseStatus.OK)
                .addHeader(CONTENT_TYPE.getValue(),
                        "text/" + fileName.getExtension() + CHARSET_UTF_8.getValue())
                .addHeader(CONTENT_LENGTH.getValue(), String.valueOf(responseBody.getBytes().length))
                .addBlankLine()
                .addBody(responseBody);
    }

    private DefaultController() {};

    public static Controller getInstance() {
        return INSTANCE;
    }
}
