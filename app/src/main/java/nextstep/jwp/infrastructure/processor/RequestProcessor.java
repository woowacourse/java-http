package nextstep.jwp.infrastructure.processor;

import nextstep.jwp.model.web.request.CustomHttpRequest;

import java.io.OutputStream;

public interface RequestProcessor {

    String processResponse(CustomHttpRequest request, OutputStream outputStream);
}
