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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

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
			String[] tokens = line.split(" ");
			
			int contentLength = 0;
			

			while(!line.equals("")) {
				line = br.readLine();
				if(line.contains("Content-Length")){
					contentLength = getContentLength(line);
				}
			}
			
			// 만약에 들어온 url이 없으면 리턴해준다 -> 무한루프 막기 위해서
			if (line == null)
				return;

			String url = tokens[1];
			
			if ("/user/create".equals(url)) {
				String body = IOUtils.readData(br, contentLength);

				//userId=admin&password=1234...로 들어왔을 때 = 을 기준으로 key:value parsing
				Map<String, String> datas = HttpRequestUtils.parseQueryString(body);
				
				//User 객체 생성 
				User user = new User(datas.get("userId"),datas.get("password"),datas.get("name"),datas.get("email"));
				log.debug("user info: {}",user);
				
				//Database에 유저 추가하기 
				DataBase.addUser(user);
				
				//response 헤더를 만들기 위해 output 객체를 만들고 이동할 url로 바꿔준다
				DataOutputStream dos = new DataOutputStream(out);
				url = "/index.html";
				response302Header(dos, url);
			}
			
			
			else if ("/user/login".equals(url)) {
				
				String body = IOUtils.readData(br, contentLength);
				
				//userId=admin&password=1234...로 들어왔을 때 = 을 기준으로 key:value parsing
				Map<String, String> datas = HttpRequestUtils.parseQueryString(body);
				
				User user = DataBase.findUserById(datas.get("userId"));
				
				//해당하는 id로 회원가입한 이력이 없을 경우 loginFail로 보내준다 
				if(user == null) {
					responseResource(out, "/user/login_failed.html");
					return ;
				}
				
				//id와 password가 모두 일치하는 경우
				if(user.getPassword().equals(datas.get("password"))) {
					DataOutputStream dos = new DataOutputStream(out);
					response302LoginSuccessHeader(dos);
				}else {
					//id는 일치하나 password는 일치하지 않는 경우
					responseResource(out, "/user/login_failed.html");
				}
				
				DataOutputStream dos = new DataOutputStream(out);
				response302LoginSuccessHeader(dos);
			}
			else {
				responseResource(out, url);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private int getContentLength(String line) {
		String[] headerTokens = line.split(":");
		return Integer.parseInt(headerTokens[1].trim());
	}
	
	
	//Code refactoring
	private void responseResource(OutputStream out, String url) throws IOException{
		DataOutputStream dos = new DataOutputStream(out);
		byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
		response200Header(dos, body.length);
		responseBody(dos, body);
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
	
	private void response302Header(DataOutputStream dos, String url) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: " + url + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void response302LoginSuccessHeader(DataOutputStream dos) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Set-Cookie: logined=true \r\n");
			dos.writeBytes("Location: " + "../index.html" + "\r\n");
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
