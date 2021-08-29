package nextstep.jwp.http.response;

import nextstep.jwp.resource.FileType;

public class TextHttpResponseBody implements HttpResponseBody {

    private final String body;

    public TextHttpResponseBody(String body) {
        this.body = body;
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public FileType fileType(){
        return FileType.HTML;
    }
}
