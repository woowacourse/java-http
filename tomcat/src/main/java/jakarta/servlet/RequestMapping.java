package jakarta.servlet;

import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.http11.HttpRequest;

import jakarta.servlet.Controller;

public class RequestMapping {

	public static List<Controller> controllers = new ArrayList<>();

	public void register(Controller handler) {
		controllers.add(handler);
	}

	public Controller getController(HttpRequest request) {
		return controllers.stream()
			.filter(controller -> controller.canHandle(request))
			.findFirst()
			.orElseThrow(()-> new IllegalArgumentException("no such Request handler"));
	}
}
