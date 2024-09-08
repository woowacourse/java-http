package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Optional;

import org.apache.coyote.http11.response.ResponseHandler;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RequestHandler {

	private final HttpRequest httpRequest;
	private final OutputStream outputStream;

	public RequestHandler(HttpRequest httpRequest, OutputStream outputStream) {
		this.httpRequest = httpRequest;
		this.outputStream = outputStream;
	}

	public void handleRequest() throws IOException {
		String httpMethod = httpRequest.getMethod();
		String urlPath = httpRequest.getPath();

		if(urlPath.endsWith("html") || urlPath.endsWith("css") || urlPath.endsWith("js")) {
			ResponseHandler.printFileResource("static" + urlPath, outputStream);
		} else if (urlPath.startsWith("/login")) {
			handleLoginRequest(httpMethod, urlPath);
		} else if (urlPath.startsWith("/register")) {
			handleRegisterRequest(httpMethod, urlPath);
		}  else {
			sendHelloWorldResponse();
		}
	}

	private void handleLoginRequest(String httpMethod, String urlPath) {
		if (urlPath.equals("/login") && httpMethod.equals("GET")) {
			Session session = new Session(httpRequest);
			SessionManager.findUserBySession(session)
				.ifPresentOrElse(
					user -> ResponseHandler.redirect("http://localhost:8080/index.html", outputStream),
					() -> ResponseHandler.printFileResource("static" + urlPath + ".html", outputStream));
		} else if (httpMethod.equals("POST")) {
			login();
		}
	}

	private void login() {
		String body = httpRequest.getBody();
		if (body != null) {
			String account = body.split("&")[0].split("=")[1];
			String password = body.split("&")[1].split("=")[1];
			User user = InMemoryUserRepository.findByAccount(account)
				.orElse(new User("guest", "guest", "guest"));
			if (user.checkPassword(password)) {
				Session session = SessionManager.createSession(user);
				ResponseHandler.redirectWithSetCookie("http://localhost:8080/index.html", session.getId(), outputStream);
				return;
			}
		}
		ResponseHandler.redirect("http://localhost:8080/401.html", outputStream);
	}


	private void handleRegisterRequest(String httpMethod, String urlPath) throws IOException {
		if (httpMethod.equals("GET")) {
			ResponseHandler.printFileResource("static" + urlPath + ".html", outputStream);
			return;
		}
		String body = httpRequest.getBody();
		if (body != null) {
			String account = body.split("&")[0].split("=")[1];
			String mail = body.split("&")[1].split("=")[1];
			String password = body.split("&")[2].split("=")[1];
			User user = new User(account, mail, password);
			InMemoryUserRepository.save(user);
			ResponseHandler.redirect("http://localhost:8080/index.html", outputStream);
		}
	}

	private void sendHelloWorldResponse() throws IOException {
		String responseBody = "Hello world!";
		String response = String.join("\r\n",
			"HTTP/1.1 200 OK",
			"Content-Type: text/html;charset=utf-8",
			"Content-Length: " + responseBody.getBytes().length,
			"",
			responseBody);
		outputStream.write(response.getBytes());
		outputStream.flush();
	}
}
