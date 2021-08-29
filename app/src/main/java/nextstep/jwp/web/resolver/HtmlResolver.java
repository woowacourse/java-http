package nextstep.jwp.web.resolver;

import static nextstep.jwp.resource.FileType.HTML;

import nextstep.jwp.resource.FilePath;
import nextstep.jwp.resource.FileReader;
import nextstep.jwp.resource.FileType;
import nextstep.jwp.web.http.MimeType;
import nextstep.jwp.web.http.response.body.HttpResponseBody;
import nextstep.jwp.web.http.response.body.TextHttpResponseBody;

public class HtmlResolver implements DataResolver {

    @Override
    public boolean isResourceExist(String url) {
        return new FilePath(url, HTML.getText()).isExist();
    }

    public boolean isExist(String url, String prefix) {
        return new FilePath(url, HTML.getText(), prefix).isExist();
    }

    @Override
    public boolean isSuitable(MimeType mimeType) {
        return FileType.findByMimeType(mimeType).contains(HTML);
    }

    @Override
    public HttpResponseBody resolve(String url){
        final FileReader fileReader = new FileReader(new FilePath(url, HTML.getText()));

        return new TextHttpResponseBody(fileReader.readAllFile(), HTML);
    }

    public HttpResponseBody resolve(String url, String prefix) {
        final FileReader fileReader = new FileReader(new FilePath(url, HTML.getText(), prefix));

        return new TextHttpResponseBody(fileReader.readAllFile(), HTML);
    }

}
