package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import nextstep.jwp.view.UserOutput;
import org.apache.coyote.common.Charset;
import org.apache.coyote.common.FileExtension;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
import org.utils.ResourceGenerator;

public class StaticResourceHandler implements BiFunction<Request, Response, Response> {

    @Override
    public Response apply(final Request request, final Response response) {
        final String responseBody;
        responseBody = ResourceGenerator.getStaticResource(request.getPath());
        UserOutput.outputUserInformation(request);
        final MediaType mediaType = MediaType.of(getFileExtension(request.getPath()));
        return response.setContentType(mediaType, Charset.UTF8)
                .setContentLength(responseBody.getBytes(StandardCharsets.UTF_8).length)
                .setBody(responseBody);
    }

    private FileExtension getFileExtension(final String path) {
        final FileExtension fileExtension = FileExtension.of(path);
        if (fileExtension.equals(FileExtension.NONE)) {
            return FileExtension.HTML;
        }
        return fileExtension;
    }
}
