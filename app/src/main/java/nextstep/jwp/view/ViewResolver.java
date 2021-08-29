package nextstep.jwp.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ViewResolver {

    public View resolve(String viewName) {
        try {
            System.out.println("==== RESOLVE RESOURCE ====");
            System.out.println(viewName);

            if(viewName.isEmpty()){
                return View.empty();
            }

            if(viewName.equals("/")){
                return View.of("Hello world!");
            }

            // TODO :: Config 분리
            final URL resourceUrl = getClass().getResource("/static" + viewName);
            final Path filePath = new File(resourceUrl.getFile()).toPath();
            return View.of(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("view not found");
        }
    }
}
