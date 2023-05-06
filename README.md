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

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답

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

### 요구사항 2 - get 방식으로 회원가입
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
### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
