package nextstep.jwp.view;

import static nextstep.jwp.resource.FileType.HTML;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.http.HttpProtocol;
import nextstep.jwp.http.MimeType;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.resource.FilePath;
import nextstep.jwp.resource.FileReader;
import nextstep.jwp.resource.FileType;

public class HtmlViewResolver implements ViewResolver {

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
    public View getView(String url) throws IOException {
        final FileReader fileReader = new FileReader(new FilePath(url, HTML.getText()));

        return createHtmlView(fileReader);
    }

    public View getView(String url, String prefix) throws IOException {
        final FileReader fileReader = new FileReader(new FilePath(url, HTML.getText(), prefix));

        return createHtmlView(fileReader);
    }

    private View createHtmlView(FileReader fileReader) throws IOException {
        HttpResponse httpResponse = HttpResponse
            .ok(
                HttpProtocol.HTTP1_1,
                HTML,
                fileReader.readAllFile()
            );

        return new HtmlView(httpResponse);
    }
}
