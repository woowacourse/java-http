package nextstep.jwp.web.resolver;

import static nextstep.jwp.resource.FileType.CSS;
import static nextstep.jwp.resource.FileType.JS;
import static nextstep.jwp.resource.FileType.SVG;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import nextstep.jwp.web.http.MimeType;
import nextstep.jwp.web.http.response.body.HttpResponseBody;
import nextstep.jwp.web.http.response.body.TextHttpResponseBody;
import nextstep.jwp.resource.FilePath;
import nextstep.jwp.resource.FileReader;
import nextstep.jwp.resource.FileType;

public class StaticResourceResolver implements DataResolver {

    private static final String DOT_PATTERN = "\\.";
    private static final String BLANK = "";
    private static final int EXIST_EXTENSION_SIZE = 2;

    @Override
    public boolean isExist(String url) {
        List<String> list = Arrays.asList(url.split(DOT_PATTERN));
        if (list.size() < EXIST_EXTENSION_SIZE) {
            return false;
        }
        return new FilePath(url, BLANK).isExist();
    }

    @Override
    public boolean isSuitable(List<String> acceptTypes) {
        List<FileType> fileTypes = FileType.findByMimeType(MimeType.findByName(acceptTypes));

        return fileTypes.contains(CSS) ||
            fileTypes.contains(JS) ||
            fileTypes.contains(SVG);
    }

    @Override
    public HttpResponseBody resolve(String url) throws IOException {
        List<String> list = Arrays.asList(url.split(DOT_PATTERN));
        FileType type = FileType.findByName(list.get(list.size() - 1));
        final FileReader fileReader = new FileReader(new FilePath(url, BLANK));

        return new TextHttpResponseBody(fileReader.readAllFile(), type);
    }
}
