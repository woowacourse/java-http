package nextstep.jwp.view;

import java.io.File;
import java.net.URL;

public class ViewResolver {

    public View resolve(String viewName) {
        try {
            System.out.println("==== RESOLVE RESOURCE ====");
            System.out.println(viewName);

            if(viewName.isEmpty()){
                return View.empty();
            }

            if(viewName.equals("/")){
                return View.asString("Hello world!");
            }

            // TODO :: Config 분리
            final URL resourceUrl = getClass().getResource("/static" + viewName);
            return View.asFile(new File(resourceUrl.getFile()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("view not found");
        }
    }
}
