package nextstep.jwp.web.resolver;

import static nextstep.jwp.resource.FileType.HTML;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.web.http.MimeType;
import nextstep.jwp.web.http.response.body.HttpResponseBody;
import nextstep.jwp.web.http.response.body.TextHttpResponseBody;
import nextstep.jwp.resource.FilePath;
import nextstep.jwp.resource.FileReader;
import nextstep.jwp.resource.FileType;

public class HtmlResolver implements DataResolver {

    @Override
    public boolean isExist(String url) {
        return new FilePath(url, HTML.getText()).isExist();
    }

    public boolean isExist(String url, String prefix) {
        return new FilePath(url, HTML.getText(), prefix).isExist();
    }

    @Override
    public boolean isSuitable(List<String> acceptTypes) {
        return FileType.findByMimeType(MimeType.findByName(acceptTypes)).contains(HTML);
    }

    @Override
    public HttpResponseBody resolve(String url) throws IOException {
        final FileReader fileReader = new FileReader(new FilePath(url, HTML.getText()));

        return new TextHttpResponseBody(fileReader.readAllFile(), HTML);
    }

    public HttpResponseBody resolve(String url, String prefix) throws IOException {
        final FileReader fileReader = new FileReader(new FilePath(url, HTML.getText(), prefix));

        return new TextHttpResponseBody(fileReader.readAllFile(), HTML);
    }

}
