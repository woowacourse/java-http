package nextstep.jwp.web.resolver;

import static nextstep.jwp.resource.FileType.HTML;

import nextstep.jwp.resource.FilePath;
import nextstep.jwp.resource.FileReader;
import nextstep.jwp.web.http.response.ContentType;
import nextstep.jwp.web.http.response.HttpResponse;

public class UriResolver implements ViewResolver {

    @Override
    public boolean isSuitable(HttpResponse response) {
        return response.isSuitableContentType(ContentType.HTML);
    }

    @Override
    public void resolve(HttpResponse response) {
        FileReader fileReader = new FileReader(
            new FilePath(response.request().methodUrl().url(), HTML.getText()));
        String body = fileReader.readAllFile();
        response.setBody(body);
        response.headers().setContentLength(body.getBytes().length);
    }
}
