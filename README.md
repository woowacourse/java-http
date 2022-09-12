# 톰캣 구현하기

## 새롭게 알게 된 지식

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

### redirect 하는 법

- 응답 헤더의 Location 필드에 redirect 할 주소 넣기!
- 상대, 절대 경로 모두 가능

### 스레드가 같은 리소스에 접근할 때 발생할 수 있는 문제

- 스레드 간섭
    - 서로 다른 스레드가 동일한 리소스에 접근하는 경우 서로 다른 스레드에서 연산한 작업을 무시하는 현상
    - ex) 어떤 값에 1 더하는 작업과 1 빼는 작업이 교차로 실행되는 경우
        - 스레드 A 0에 1 더했으니 1 저장
        - 스레드 B 0에 1 뺐으니 -1 저장
        - 스레드 B가 스레드 A가 한 작업을 무시한 채로 덮어쓰게 됨!
- 메모리 일관성 오류
    - 다른 스레드가 일관성 없이 같은 데이터를 바라보는 경우
    - ex) 스레드 A는 ++연산, B는 출력을 한다면 B가 A의 연산 결과를 항상 볼 수 있다는 보장이 없다.
    - happens before relationship
        - 공유되고있는 리소스가 다른 스레드에 의해 변경된 경우 변경된 값을 읽을 수 있도록 하는 것
- 해결법 ( Thread safe class )
    - 상태 변수를 스레드 간에 공유하지 않는다.
    - 상태 변수를 변경할 수 없도록 한다.
    - 상태 변수를 접근할 때에는 언제나 동기화를 사용한다.

### 동기화 하기

- `synchronized` 키워드 붙이기

    ```
    public synchronized void A(){}
    ```

- 한 스레드가 해당 객체의 메서드를 실행중이라면 다른 스레드는 block 된다.
- 생성자에서는 사용 불가능! 생성되는 동안은 아직 생성되지 않았으므로 다른 스레드에서 접근이 불가능하기 때문에 추가적인 키워드 필요없기 때문

### 자바에서 Thread만들기

- Thread 클래스를 상속하여 만들기
    - run()을 오버라이드하여 원하는 작업을 지정하고 start()를 통해 호출한다.

    ```
    private static final class ExtendedThread extends Thread {
    
       private String message;
    
       public ExtendedThread(final String message) {
           this.message = message;
       }
    
      @Override
      public void run() {
           log.info(message);
      }
    }
    
    Thread thread = new ExtendedThread("hello thread");
    ```

- Runnable 인터페이스 사용하기
    - run()을 오버라이드하여 원하는 작업을 지정하고 Thread의 생성자 파라미터로 전달하여 start()를 통해 호출한다.

    ```
    private static final class RunnableThread implements Runnable {
    
        private String message;
    
        public RunnableThread(final String message) {
            this.message = message;
        }
    
        @Override
         public void run() {
            log.info(message);
        }
    }
    
    Thread thread = new Thread(new RunnableThread("hello thread"));
    ```

- 두가지 방법이 존재하는 이유
    - 자바는 다중 상속이 불가능하기 때문!
        - 스레드로 구현할 클래스가 있고, 해당 클래스가 스레드 이외의 클래스도 상속받아야한다면 사용이 불가능하다!
        - interface는 다중구현이 가능하므로 Runnable을 구현하여 사용할 수 있다.

### Thread의 주요 메서드

- start()
    - 스레드 실행 시작
    - NEW 상태의 스레드를 RUNNABLE 상태로 만들며 스레드를 대기 큐에 넣는다.
- sleep()
    - 파라미터로 넘어온 시간만큼 스레드를 대기시킨다.
    - InterruptedException 처리 필요
- join()
    - 해당 스레드가 죽는 것을 기다린다.
    - 파라미터로 특정 시간값을 넘기면 해당 시간만큼만 기다린다. ( 기본값은 0이며 계속 기다리겠다는 의미 )
    - InterruptedException 처리 필요

### Thread Pool

- 스레드를 미리 생성해놓고 작업 큐에 들어오는 작업들을 하나씩 스레드에 할당하여 처리하는 것
- Fixed Thread Pool
    - 항상 지정된 수의 스레드가 실행 중
    - 스레드를 매번 생성하지 않으므로 응답이 매우 빠르다.
    - 모든 스레드가 사용 중이라면 새 작업은 대기열에 넣는다.
    - 실행시간을 예측할 수 없는 작업에 적합하다.
- Cached Thread Pool
    - 이전에 생성된 스레드가 사용가능하다면 재사용한다.
    - 사용 가능한 스레드가 없다면 새롭게 생성한다.
    - 합리적인 개수를 가지는 단기 작업에 유리하다.
    - I/O등 실행시간을 예측할 수 없는 경우 적합하지 않다.

### accept-count

- request queue의 길이
- request 요청시 실행 가능한 스레드가 존재하지 않으면 대기큐에 메시지 형태로 들어가게 된다. 이 대기큐의 개수를 지정하는 것!
- 기본값은 100이다

### max-connections

- 서버가 허용할 수 있는 최대 커넥션 수
- 최대 커넥션 수에 도달하면 해당 요청은 ( 메시지는 ) 큐에 들어간다.
- Connector 방식에 따라 기본값이 다르다.
    - BIO ( Blocking IO )
        - connection 하나에 thread 하나 ( 톰캣 9부터 지원되지 않음 )
        - 기본값 ) threads.max와 같은 값.
    - NIO ( Non-Blocking IO )
        - connection에 thread를 바로 할당하는 것이 아니라, 필요할 때에만 할당 ( 톰캣 6에서 추가 )
        - 기본값 ) 10000
    - APR
        - 성능 개선을 위해 java가 아닌 Native 언어로 작성 ( 톰캣 5.5에서 추가되었으나 기본으로 함께 설치되어있지는 않음. 별도 설치 )
        - 기본값 ) 8192

### threads.max

- 톰캣 내의 최대 쓰레드 수 ( 그 순간 처리 가능한 트랜잭션의 수 )
- 너무 많이 설정한다면 스레드간의 문맥교환으로 성능이 저하될 수 있다.
- 너무 적게 설정한다면 사용되지 않는 CPU가 낭비된다.
- 기본값은 200이다.

### Executor

- Runnable 객체를 실행시키는 메서드를 제공하는 인터페이스
- 구현체에 따라 어떻게 스레드를 할당할지, 스케쥴링 할지 등등을 결정할 수 있다.
- 과거에는 Connector 하나당 하나의 스레드 풀이 생성되었지만 Executor를 활용하면 스레드 풀을 공유할 수 있다.

### ExecutorService

- Executor를 상속받는 인터페이스
- 스레드의 종료를 관리하는 메서드와 비동기 작업의 실행 과정을 추적하기 위한 Future 객체를 생산하는 메서드 등을 추가 제공하는 스레드 풀!
- ThreadPoolExecutor나 Executors의 정적 팩토리 메서드를 이용하여 생성할 수 있다.
    - `Executors.newCachedThreadPool()`
    - `Executors.newFixedThreadPool(100)`
- maxThreads 등의 설정이 포함되므로 다른 곳에서 설정을 해도 무시된다.
- 작업 할당을 위한 메서드
    - execute()
        - return type이 void이기 때문에 실행 결과나 상태 등은 알 수 없다.
    - submit()
        - task를 할당하고 Future 타입의 결과값을 받는다.
    - invokeAny()
        - task를 Collection에 넣어서 인자로 넘기면 실행에 성공한 task 중 하나의 리턴값을 반환한다.
    - invokeAll()
        - task를 Collection에 넣어서 인자로 넘기면 모든 task의 리턴 값을 List<Future<>>로 반환한다.
- ExecutorService 종료를 위한 메서드
    - shutdown()
        - 실행중인 모든 task가 수행 종료되면 종료
    - shutdownNot()
        - 실행중인 스레드 즉시 종료
        - 하지만 모든 스레드가 동시에 종료되는 것을 보장하지는 않음
        - 실행되지 않은 task 반환
    - awaitTermination()
        - 새로운 task가 실행되는 것을 막고, 일정 시간동안 task가 완료되기를 기다린다.
        - 만일 일정 시간동안 완료되지 않는다면 강제로 종료시킨다.

### 동시성 컬렉션

- List, Queue, Map같은 표준 컬렉션 인터페이스에 동시성 추가하여 구현한 고성능 컬렉션
- ConcurrentHashMap
    - 동시성이 뛰어나며 속도도 무척 빠르다
    - Conllections.synchronizedMap 보다는 ConcurrentHashMap 을 사용하는게 훨씬 좋다.
