package nextstep.jwp.http.response;

import nextstep.jwp.resource.FileType;

public class TextHttpResponseBody implements HttpResponseBody {

    private final String body;
    private final FileType fileType;

    public TextHttpResponseBody(String body, FileType fileType) {
        this.body = body;
        this.fileType = fileType;
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public FileType fileType(){
        return fileType;
    }
}
