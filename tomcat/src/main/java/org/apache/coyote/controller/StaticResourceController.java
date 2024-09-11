//package org.apache.coyote.controller;
//
//import org.apache.coyote.request.HttpRequest;
//import org.apache.coyote.response.HttpResponse;
//import org.apache.coyote.util.FileTypeChecker;
//import org.apache.coyote.view.StaticResourceResolver;
//
//public class StaticResourceController extends AbstractController {
//
//    @Override
//    protected void doGet(HttpRequest request, HttpResponse response) {
//        String targetPath = request.getTargetPath();
//        if (FileTypeChecker.isSupported(targetPath)) {
//            new StaticResourceResolver().resolve(targetPath, response);
//        }
//    }
//}
