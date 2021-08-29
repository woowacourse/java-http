package nextstep.jwp.service;

import java.io.IOException;
import nextstep.jwp.util.HeaderLine;
import nextstep.jwp.util.HttpResponse;

public class ExtraService implements Service {

    public ExtraService() {

    }
    @Override
    public String process(HeaderLine headerLine) throws IOException {
        HttpResponse httpResponse = new HttpResponse(headerLine);
        return httpResponse.getResponse();
    }
}
