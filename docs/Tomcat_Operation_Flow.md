### 톰캣 서버 시작

- Application.main()으로 톰캣 서버 프로그램 시작
    - Tomcat tomcat 생성
    - tomcat.start() 메서드 실행
- tomcat.start()
    - Connector connector 생성
        - DEFAULT_PORT(8080), DEFAULT_ACCEPT_COUNT(100)으로 ServerSocket 생성
        - 생성된 ServerSocket을 serverSocket 필드로 유지하고 있음
    - connector.start() 메서드 실행
- connector.start()
    - tomcat 자신을 새로운 스레드로 실행 (Connectors는 Runnable을 상속받기에 스레드로 실행 가능)
    - "Web Application Server started 8080 port." 로그 출력
- tomcat 스레드 동작 방식
    - 새롭게 생성된 스레드는 run() 메서드를 실행한다.
    - run() 메서드 내부에서는 connect()을 무한 반복하며 클라이언트의 요청을 기다림
- tomcat 스레드의 connect()
    - serverSocket.accept()으로 요청이 도착했는지 체크
    - 요청이 도착하지 않았다면 serverSocket.accept()는 null을 리턴하고 루프를 타고 다시 serverSocket.accept()) <= 요청을 기다리는
      동작
    - 요청이 도착했다면 serverSocket.accept()는 클라이언트와의 연결 객체인 Socket을 생성해서 리턴
    - 생성된 Socket으로 Http11Processor 스레드를 생성하고 실행 (즉, Http11Processor.run() 실행)
- Http11Processor 스레드의 run()
    - 클라이언트와의 연결정보를 알리는 "connect host: 클라이언트_주소, port: 클라이언트_포트") 로그 출력
    - socket으로 부터 클라이언트와 통신할 InputStream과 OutputStream 받아오기
    - InputStream을 통해 클라이언트의 요청 메세지를 읽고, OutputStream으로 클라이언트에게 보낼 응답 메세지를 보낸다.
    - Http11Processor 스레드가 종료되면서 요청 처리가 완료되고, tomcat.run()으로 되돌아간다.

---

### ServerSocket

- ServerSocket 이란?
    - 클라이언트와의 연결 객체인 Socket을 생성하는 객체
    - 클라리언트의 요청이 도착하면 해당 클라이언트와 데이터통신을 수행하는 Socket을 생성한다.

- ServerSocket의 동작 방식
    - serverSocket.accept()
        - 현재 연결 요청이 있으면 연결 객체인 Socket을 생성해서 리턴
        - 현재 연결 요청이 없으면 null 리턴

### Socket

- Socket
    - 클라이언트와의 실제데이터 통신을 수행하는 연결 객체
- Socket의 동작 방식
    - socket.getInputStream()
        - 클라이언트가 보낸 요청메세지를 읽는 InputStream을 불러온다.
    - socket.getOutputStream()
        - 클라이언트에게 보낼 응답메세지를 읽는 OutputStream을 불러온다.

---

### 미션 1단계를 구현하는 방안

- 간단한 if문으로 Uri마다의 동작을 수행하는 방식
- 


