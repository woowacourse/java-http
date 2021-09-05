package nextstep.jwp.framework.file;

import nextstep.jwp.framework.exception.HtmlNotFoundException;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class StaticFileFinder {

    private static final String SLASH = "/";
    private static final String DEFAULT_PATH = "./static";

    private final String path;

    public StaticFileFinder(String path) {
        this.path = path;
    }

    public StaticFile find() {
        String filePath = makeFilePath(path);
        URL resource = getClass().getClassLoader().getResource(filePath);
        validateResource(resource, path);
        File file = new File(resource.getPath());
        return new StaticFile(file);
    }

    private String makeFilePath(String path) {
        if (path.startsWith(SLASH)) {
            return DEFAULT_PATH + path;
        }
        return DEFAULT_PATH + SLASH + path;
    }

    private void validateResource(URL resource, String path) {
        if (Objects.nonNull(resource)) {
            return;
        }
        FileExtension fileExtension = FileExtension.extractExtension(path);
        if (fileExtension.match(FileExtension.HTML)) {
            throw new HtmlNotFoundException(String.format("HTML 파일을 찾지 못했습니다. (%s)", path));
        }
        throw new HtmlNotFoundException(String.format("정적 파일을 찾지 못했습니다. (%s)", path));
    }
}
