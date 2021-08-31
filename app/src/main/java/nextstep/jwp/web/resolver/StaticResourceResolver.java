package nextstep.jwp.web.resolver;

import static nextstep.jwp.resource.ExtensionExtractor.extract;
import static nextstep.jwp.resource.FileType.CSS;
import static nextstep.jwp.resource.FileType.HTML;
import static nextstep.jwp.resource.FileType.JS;
import static nextstep.jwp.resource.FileType.SVG;

import nextstep.jwp.resource.FilePath;
import nextstep.jwp.resource.FileReader;
import nextstep.jwp.resource.FileType;
import nextstep.jwp.web.http.response.ContentType;
import nextstep.jwp.web.http.response.HttpResponse;

public class StaticResourceResolver implements ViewResolver {

    private static final String BLANK = "";

    @Override
    public boolean isSuitable(HttpResponse response) {
        String extension = extract(response.request().methodUrl().url());
        if (extension.isEmpty()) {
            return false;
        }
        FileType type = FileType.findByName(extension);

        return type == HTML ||
            type == CSS ||
            type == SVG ||
            type == JS;
    }

    @Override
    public void resolve(HttpResponse response) {
        String url = response.request().methodUrl().url();

        FileReader fileReader = new FileReader(
            new FilePath(url, BLANK));
        String body = fileReader.readAllFile();
        response.setBody(body);

        ContentType type = ContentType.findByExtensionName(extract(url));
        response.headers().setContentType(type);
        response.headers().setContentLength(body.getBytes().length);
    }

}
