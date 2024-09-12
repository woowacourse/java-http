# 만들면서 배우는 스프링

## 톰캣 구현하기

### 구현하면서 궁금했던 점

1. Http11Processor의 이 부분을

``` 
String line = bufferedReader.readLine();

line = line.split(" ")[1];

final URL resource = getClass().getResource("/static" + line);
```

``` 
String line = bufferedReader.readLine();

line = line.split(" ")[1];

final URL resource = getClass().getResource("/static/index.html");
```

이렇게 적으면 `localhost:8080/index.html`을 호출했을 때 디스플레이 되는 페이지 모습이 달랐습니다.
(전자: 그래프 노출되지 않음)
(후자: 그래프 노출됨)

정확한 이유가 뭘까요? 🤔

2. 3단계 리팩토링 단계 전까지는 클래스 분리 없이 모든 코드를 Http11Processor에 억지로 구겨 넣어보려고 합니다. HttpStatus나 HttpRequest, Response 등으로 분리할 수 있는
   부분이 슬슬 보이고 있는데요. 해당 분리는 모두 3단계에 몰아서 진행해보려고 해요! 코드에 `String[]` 나 과도한 `if`문들, 끝을 모르고 길어지는 메서드 라인들이 점점 늘어가고 있는데, 2단계
   코드리뷰 때까지는 양해해주셨으면 합니다!


3. 세션/쿠키의 디렉토리 위치를 현재는 코요테 밑 `http`에 두고 있는데, 맞는 위치인지 잘 모르겠어요...
   <br><br><br><br><br>

### 학습목표

- 웹 서버 구현을 통해 HTTP 이해도를 높인다.
- HTTP의 이해도를 높혀 성능 개선할 부분을 찾고 적용할 역량을 쌓는다.
- 서블릿에 대한 이해도를 높인다.
- 스레드, 스레드풀을 적용해보고 동시성 처리를 경험한다.

### 시작 가이드

1. 미션을 시작하기 전에 파일, 입출력 스트림 학습 테스트를 먼저 진행합니다.
    - [File, I/O Stream](study/src/test/java/study)
    - 나머지 학습 테스트는 다음 강의 시간에 풀어봅시다.
2. 학습 테스트를 완료하면 LMS의 1단계 미션부터 진행합니다.

## 학습 테스트

1. [File, I/O Stream](study/src/test/java/study)
2. [HTTP Cache](study/src/test/java/cache)
3. [Thread](study/src/test/java/thread)
