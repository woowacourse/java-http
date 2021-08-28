package nextstep.jwp.view;

import java.io.IOException;
import nextstep.jwp.http.HttpProtocol;
import nextstep.jwp.http.response.HtmlView;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.resource.FilePath;
import nextstep.jwp.resource.FileReader;
import nextstep.jwp.resource.FileType;

public class HtmlViewResolver implements ViewResolver {

    private static final String DOT = ".";
    private static final String BLANK = "";
    private static final int NOT_FOUND = -1;
    private static final int NOT_FOUND_INDEX = NOT_FOUND;

    @Override
    public boolean isSuitable(String fileName) {
        String extensionPrefix = subtractExtensionPrefix(fileName);
        if (extensionPrefix.isEmpty()) {
            return false;
        }
        return FileType.HTML == FileType.findByName(extensionPrefix);
    }

    @Override
    public View getView(String filePath) throws IOException {
        final FileReader fileReader = new FileReader(new FilePath(filePath));

        return createHtmlView(filePath, fileReader);
    }

    public View getView(String filePath, String prefix) throws IOException {
        final FileReader fileReader = new FileReader(new FilePath(filePath, prefix));

        return createHtmlView(filePath, fileReader);
    }

    private View createHtmlView(String filePath, FileReader fileReader) throws IOException {
        HttpResponse httpResponse = HttpResponse
            .ok(
                HttpProtocol.HTTP1_1,
                FileType.findByName(subtractExtensionPrefix(filePath)),
                fileReader.readAllFile()
            );

        return new HtmlView(httpResponse);
    }

    private String subtractExtensionPrefix(String fileName) {
        int dotIndex = fileName.lastIndexOf(DOT);
        if (dotIndex == NOT_FOUND_INDEX) {
            return BLANK;
        }
        return fileName.substring(dotIndex + 1);
    }
}
