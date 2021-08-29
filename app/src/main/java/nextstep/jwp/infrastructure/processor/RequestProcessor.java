package nextstep.jwp.infrastructure.processor;

import nextstep.jwp.model.CustomHttpRequest;

import java.io.OutputStream;

public interface RequestProcessor {

    public String processResponse(CustomHttpRequest request, OutputStream outputStream);
}
