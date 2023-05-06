# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

## 요구사항 1 - http://localhost:8080/index.html로 접속시 응답

</br>
</br>

> RequestHandler 수정전.java

![](https://velog.velcdn.com/images/1_kyun/post/ea3b1dbc-a06c-4616-982b-001c232b0693/image.png)

</br>
</br>

```java
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); 
        			OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "Hello World".getBytes();
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
```

</br>
</br>




> RequestHandler 수정후.java

![](https://velog.velcdn.com/images/1_kyun/post/09ca4b88-0762-4b7e-8bdb-2602bcad1ac2/image.png)

</br>
</br>

```java
public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", 
        connection.getInetAddress(),connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            
            //1.사용자가 입력한 URL를 'UTF-8' 방식으로 받는다
        	BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        	String line = br.readLine();
        	
        	//log.debug("request line : {}",line);
        	
        	//2. 만약에 들어온 url이 없으면 리턴해준다 -> 무한루프 막기 위해서 
        	if(line == null)return;
        	
            //3.들어온 라인을 공백기준으로 파싱해준다
        	String [] tokens = line.split(" ");
        	
        	/*while(!"".equals(line)) {
        		line = br.readLine();
        	}*/
        	
            //4.URL로 부터 들어온 토큰들 확인 
        	for(int i = 0; i < tokens.length; i++) {
        		log.debug(tokens[i]);
        	}
        	
            
            DataOutputStream dos = new DataOutputStream(out);
            
            //5. file을 읽을 수 있게 Byte로 webapp + /index.html를 읽게 한다
            byte[] body = Files.readAllBytes(new File("./webapp"+tokens[1]).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
```
</br>
</br>
</br>
</br>

- 4번에서 들어온 url 토큰 목록 
![](https://velog.velcdn.com/images/1_kyun/post/c5e5501d-4487-48f9-a746-71ff8e4db7f9/image.png)

우리가 사용할 것은 token[1]에 있는 /index.html

<br>
<br>

## 요구사항 2 - get 방식으로 회원가입
</br>

> - http://localhost:8080/user/create?userId=admin&password=1234&name=%EC%A0%95%EC%9B%90%EA%B7%A0&email=wjddnjsrbs97%40naver.com 
와 같은 형식으로 회원가입 요청이 GET방식으로 들어온다 (URL)

![](https://velog.velcdn.com/images/1_kyun/post/d6fcf1b5-bfc8-4c33-8af8-071561599576/image.png)

</br>
</br>

수정전에는 URL이 들어와도 아무런 매핑을 해주지 않는데 GET 방식으로 들어온 회원가입 URL을 파싱해서 User 객체에 저장해보자


> RequestHandler 수정전.java

```java
public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", 
        connection.getInetAddress(),connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        	String line = br.readLine();
        	
        	if(line == null)return;
        	
            //3.들어온 라인을 공백기준으로 파싱해준다
        	String [] tokens = line.split(" ");
            
            DataOutputStream dos = new DataOutputStream(out);
            
            byte[] body = Files.readAllBytes(new File("./webapp"+tokens[1]).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
```

</br>
</br>

> RequestHandler 수정후.java

```java
public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", 
        connection.getInetAddress(),connection.getPort());

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
				
				//4. ?이후에 userId=admin&password=1234...로 들어왔을 때 
                //= 을 기준으로 key:value parsing
				Map<String, String> datas = HttpRequestUtils.parseQueryString(pointUrl);
				
				//5. User 객체 생성 
				User user = new User(datas.get("userId"),datas.get("password"),
                										datas.get("name"),datas.get("email"));
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
```
</br>
</br>

- 객체 생성 완료

![](https://velog.velcdn.com/images/1_kyun/post/276b70f4-1be0-4d05-89fd-3eaad6c689fb/image.png)
</br>
</br>
## 요구사항 3 - post 방식으로 회원가입

</br>

> 요구사항 2번에서 form으로 Get 방식으로 받아서 url parsing을 통해 객체를 생성했다. 이번엔 Post 방식을 이용해서 Http 본문을 parsing 해서 객체를 생성해본다.


> RequestHandler 수정전.java

</br>

```java
public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", 
        connection.getInetAddress(),connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	
            BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        	String line = br.readLine();
        	
        	if(line == null)return;
        	
        	String [] tokens = line.split(" ");
            
            DataOutputStream dos = new DataOutputStream(out);
            
            byte[] body = Files.readAllBytes(new File("./webapp"+tokens[1]).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
```

</br>
</br>

> RequestHandler 수정후.java

```java
public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();
			String[] tokens = line.split(" ");
			
           	//1. form을 통해 받은 회원가입 정보가 들어있는 http 본문 길이
			int contentLength = 0;
			
            System.out.println("출력 시작");
			System.out.println("-----------------------------------");
			
            while(!line.equals("")) {
				line = br.readLine();
				System.out.println(line);
				
                2. 길이에 대한 정보는 Content-Length: 90 이런식으로 나옴
                if(line.contains("Content-Length")){
					contentLength = getContentLength(line);
				}
			}
			
            System.out.println("-----------------------------------");
			System.out.println("출력 종료");
			
			// 만약에 들어온 url이 없으면 리턴해준다 -> 무한루프 막기 위해서
			if (line == null)
				return;

			String url = tokens[1];
			
			//4. user/create로 들어올 때 처리해줌 (post방식은 url이 달라지지 않음)
			if ("/user/create".equals(url)) {
				String body = IOUtils.readData(br, contentLength);

				//userId=admin&password=1234...로 들어왔을 때 = 을 기준으로 key:value parsing
				Map<String, String> datas = HttpRequestUtils.parseQueryString(body);
				
				//User 객체 생성 
				User user = new User(datas.get("userId"),datas.get("password"),datas.get("name"),
           								datas.get("email"));
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
    
    //3. http 메세지 parsing, message : information 으로 들어오는 것을 파싱해준다 
    private int getContentLength(String line) {
		String[] headerTokens = line.split(":");
		return Integer.parseInt(headerTokens[1].trim());
	}
```

- 요청시 들어오는 HTTP  본문은 다음과 같고 우리는 여기서 요청의 본문 길이인 90에 대해서 읽어주면 된다

![](https://velog.velcdn.com/images/1_kyun/post/1dfef949-9f4c-4af5-a1c1-6db1c78d4be4/image.png)

- 객체 생성완료
![](https://velog.velcdn.com/images/1_kyun/post/ddbef014-c365-4e39-a660-fb3fee2e2d3b/image.png)

</br>
</br>

## 요구사항 4 - redirect 방식으로 이동

> "회원가입"을 완료하면 /index.html 페이지로 이동 현재 /user/create로 유지되는 상태이기 때문에 응답으로 전달할 파일이 없음. 회원가입을 완료한 후 /index.html 페이지로 이동하게 한다.

> RequestHandler 수정전.java

</br>

```java
public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();
			String[] tokens = line.split(" ");
            
			int contentLength = 0;
			
            System.out.println("출력 시작");
			System.out.println("-----------------------------------");
			
            while(!line.equals("")) {
				line = br.readLine();
				System.out.println(line);
				
                if(line.contains("Content-Length")){
					contentLength = getContentLength(line);
				}
			}
			
            System.out.println("-----------------------------------");
			System.out.println("출력 종료");
			
			if (line == null)
				return;

			String url = tokens[1];
			
			if ("/user/create".equals(url)) {
				String body = IOUtils.readData(br, contentLength);

				//userId=admin&password=1234...로 들어왔을 때 = 을 기준으로 key:value parsing
				Map<String, String> datas = HttpRequestUtils.parseQueryString(body);
				
				//User 객체 생성 
				User user = new User(datas.get("userId"),datas.get("password"),datas.get("name"),
           								datas.get("email"));
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
    
    private int getContentLength(String line) {
		String[] headerTokens = line.split(":");
		return Integer.parseInt(headerTokens[1].trim());
	}
```

</br>
</br>


> RequestHandler 수정후.java

```java
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
				
				//1. response 헤더를 만들기 위해 output 객체를 만들고 이동할 url로 바꿔준다
				DataOutputStream dos = new DataOutputStream(out);
				url = "/index.html";
				response302Header(dos, url);
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
    
//2. 200 Header에서 302 Header로 변경후 요청 url로 redirect 시켜준다 
private void response302Header(DataOutputStream dos, String url) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: " + url + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
    
```

## 요구사항 5 - cookie

> 로그인이 성공하면 /index.html로 이동하고, 로그인이 실패하면 /user/login_failed.html로 이동한다 -> 여기서 앞에서 회원가입한 사용자로 로그인 할 수 있어야 하며 로그인이 성공할 경우 요청 헤더의 Cookie 헤더 값이 logined=true, 로그인이 실패하면 Cookie 헤더 값이 logined=false로 전달한다.

</br>

> RequestHandler 수정전.java

```java
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
				
				DataOutputStream dos = new DataOutputStream(out);
				url = "/index.html";
				response302Header(dos, url);
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
    
private void response302Header(DataOutputStream dos, String url) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: " + url + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
    
```

</br>
</br>

> RequestHandler 수정후.java


```java

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
			
			
            //1. login form 요청이 들어올 때 
			else if ("/user/login".equals(url)) {
				
				String body = IOUtils.readData(br, contentLength);
				
				//userId=admin&password=1234...로 들어왔을 때 = 을 기준으로 key:value parsing
				Map<String, String> datas = HttpRequestUtils.parseQueryString(body);
				
                //2. data의 userId값을 받아 DB안에 해당 id를 가진 객체가 있는지 확인
				User user = DataBase.findUserById(datas.get("userId"));
				
				//3. 해당하는 id로 회원가입한 이력이 없을 경우 loginFail로 보내준다 
				if(user == null) {
					responseResource(out, "/user/login_failed.html");
					return ;
				}
				
				//4. id와 password가 모두 일치하는 경우
				if(user.getPassword().equals(datas.get("password"))) {
					DataOutputStream dos = new DataOutputStream(out);
					response302LoginSuccessHeader(dos);
				}else {
					//5. id는 일치하나 password는 일치하지 않는 경우
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
	
	
	//6. Code refactoring
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
	
    7. 로그인이 성공했을 때 Cookie를 설정하고 redirect할 경로를 설정
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

```
</br>
</br>

- 로그인 성공 시 index.html로 이동한다

- 로그인 실패 시 다음과 같은 login_Failed.html로 보내준다 

![](https://velog.velcdn.com/images/1_kyun/post/1d8b79b8-79b4-4600-9c57-9539234dd261/image.png)




### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
