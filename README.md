# 톰캣 구현하기

## 1단계

- 구현 기능 목록
    - GET /index.html 응답하기
    - CSS 지원하기
    - Query String 파싱

## 1단계를 통해 새롭게 알게 된 지식

### resource 파일 경로 알기

  ```
  getClass()
  .getClassLoader()
  .getResource(fileName) // URI 타입 반환
  .toString() // String으로 변환
  ```

### 파일 내용 읽기

- Path
  : 파일 혹은 경로 정보
    - 주요 메서드
        - Paths.get(”uri”) : 경로 정보를 통해 path 생성
        - toString() : 전체 경로 반환
        - toFile() : File 타입으로 변환
        - toURI() : URI 객체로 변환
        - resolve(”uri”) : 해당 path에 경로 추가하기
- Files
  : 파일 존재 유무, 읽기 쓰기, 디렉토리 생성 등 가능
    - 주요 메서드
        - List<String> Files.readAllLines(path) : 파일 내용 전체 담기
        - Files.createDirectory(path) : 디렉토리 생성
        - Files.copy(source, target) : 파일 복사
        - toPath() : Path 객체로 변환
- resource 파일의 내용 읽기 구현
    ```
    // 경로 정보 찾기
    String uri = Paths.get(getClass()
    .getClassLoader()
    .getResource(fileName)
    .toURI());

    // 경로 정보를 통해 path 생성
    Path path = Paths.get(url);
    
    // 파일 내용 읽기
    List<String> content = Files.readAllLines(path);
    ```

### I/O Stream

- OutputStream
    ```
    // 선언
    final OutputStream outputStream = new ByteArrayOutputStream();
    
    // 쓰기
    byte[] bytes = {110, 101, 120, 116, 115, 116, 101, 112};
    outputStream.write(bytes);
    
    // String으로 변환
    String str = outputStream.toString();
    
    // 강제로 버퍼 내용 전송
    outputStream.flush();
    
    // 사용 종료
    outputStream.close();
    ```
- InputStream
    ```
    // 선언
    final OutputStream outputStream = new ByteArrayOutputStream();
    
    // 쓰기
    byte[] bytes = {110, 101, 120, 116, 115, 116, 101, 112};
    outputStream.write(bytes);
    
    // String으로 변환
    String str = outputStream.toString();
    
    // 강제로 버퍼 내용 전송
    outputStream.flush();
    
    // 사용 종료
    outputStream.close();
    ```

### FilterStream

: InputStream, OutputStream에 연결되어 읽거나 쓰는 데이터를 수정 ( 암호화, 압축, 포맷 변환 등 )

- reader와 writer 존재
- 바이트를 다른 데이터 형식으로 변환할 수 있다.
- ex) UTF-8, ISO 8859-1 같은 형식으로 인코딩된 텍스트를 처리
- 버퍼의 크기를 지정해주지 않는 경우 기본적으로 `8192byte`
- 사용법
    ```
    // 연결할 inputStream 생성
    final InputStream inputStream = new ByteArrayInputStream(text.getBytes());
                  
    // BufferedInputStream 선언 ( FilterStream를 extends 함 )
    final InputStream bufferedInputStream = new BufferedInputStream(inputStream);
      
    // 읽기
    final byte[] actual = bufferedInputStream.readAllBytes();
      
    // BufferedReader를 이용하여 읽기
    final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
    br.readLine()
    ```

### try-with-resources ( java 9 이상 지원 )

: 사용된 자원을 자동으로 반납해주는 기능

- Stream은 동기로 동작하기 때문에
    - 버퍼가 가득 찰 때까지 flush하지 않는다면 데드락 상태가 될 수 있다.
    - close()하여 스트림을 닫지 않는다면 파일, 포트 등 다양한 리소스에서 누수가 발생할 수 있다.
- ⇒ 이를 매번 할 필요 없도록 자동으로 반납해주는 try-with-resources 이용하기!
- 사용법
    ```
    try(
    // 필요한 자원 선언
    OutputStream outputStream = new ByteArrayOutputStream()
    ){
    // 자원 사용~~~
    ...
    } catch(Exception e){
    }
    ```

- 꼭 try문 안에서 선언할 필요 없음!
    ```
    final OutputStream outputStream = new ByteArrayOutputStream()
    try( outputStream ){
    // 자원 사용~~~
    ...
    }catch(Exception e){
    }
    ```

### HTTP Request의 구조

- Start Line
    ```
    GET /index.html HTTP/1.1
    ```
    - HTTP method  : GET, POST, PUT, DELETE 등 요청 메서드
    - Request target : 전송되는 목표 주소
    - HTTP version : 버전 정보
- Headers
    ```
    Host: localhost:8080
    Accept: text/html
    Connection: keep-alive
    ```
    - Host : 요청하려는 서버 호스트 이름과 포트 번호
    - User-agent : 클라이언트 프로그램 정보
    - Accept :  클라이언트가 처리 가능한 미디어 타입 종류 나열
    - Authorization : 인증 토큰
    - Origin : 서버로 Post 요청을 보낼 때 요청이 어느 주소에 시작되었는지 나타내는 값.
    - Cookie : 쿠키 값이 key-value 형태로 표현
- Body
    ```
    {
        "data" : "data"
    }
    ```
    - 전송하려는 데이터

### HttpResponse 구조

- Status Line
    ```
    HTTP/1.1 200 OK
    ```
    - HTTP version
    - Status Code
    - Status Text
- Headers
- Body

## 2단계를 통해 새롭게 알게 된 지식

### redirect 하는 법

- 응답 헤더의 Location 필드에 redirect 할 주소 넣기!
- 상대, 절대 경로 모두 가능
