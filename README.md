# 톰캣 구현하기

## 3단계 - 리팩터링
- [x] HttpRequest 클래스 구현하기
- [x] HttpResponse 클래스 구현하기
- [x] Controller 인터페이스 추가하기
    - [x] AbstractController를 상속한 구현체 만들기

## 4단계 - 동시성 확장하기
- [x] Executors로 Thread Pool 적용하기
    - [x] Connector 클래스에서 Executors 클래스를 사용해서 ExecutorService 객체 생성하기
    - [x] maxThreads라는 변수로 스레드 갯수 지정하기
- [x] 동시성 컬렉션 사용하기
    - [x] SessionManager 클래스에서 Session 컬렉션에 동시성 컬렉션 적용하기
