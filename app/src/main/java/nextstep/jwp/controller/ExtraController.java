package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.util.HeaderLine;

public class ExtraController implements Controller {

    @Override
    public String process(HeaderLine headerLine) throws IOException {
        HttpResponse httpResponse = new HttpResponse(headerLine);
        return httpResponse.getResponse();
    }
}
