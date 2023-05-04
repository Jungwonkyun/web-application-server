package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import util.HttpRequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();

			// log.debug("request line : {}",line);

			// 만약에 들어온 url이 없으면 리턴해준다 -> 무한루프 막기 위해서
			if (line == null)
				return;
			
			String[] tokens = line.split(" ");
			
			String url = tokens[1];
			
			///1. user/create로 시작하는 URL이 들어올 때 처리해줌 
			if (url.startsWith("/user/create")) {
				//2. create?~~ 에서 ?의 위치를 저장
				int index = url.indexOf("?");
				
				//3. ?이후의 url을 핵심 url로 자른다
				String pointUrl = url.substring(index+1);
				
				//4. ?이후에 userId=admin&password=1234...로 들어왔을 때 = 을 기준으로 key:value parsing
				Map<String, String> datas = HttpRequestUtils.parseQueryString(pointUrl);
				
				//5. User 객체 생성 
				User user = new User(datas.get("userId"),datas.get("password"),datas.get("name"),datas.get("email"));
				log.debug("user info: {}",user);
			}
			
			else {
				DataOutputStream dos = new DataOutputStream(out);
				byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
				response200Header(dos, body.length);
				responseBody(dos, body);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
