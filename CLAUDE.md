# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

Spring Boot 4.1.1 기반 Java 애플리케이션. [스프링 핵심 원리 - 기본편] 강의 실습 저장소로, 회원·주문·할인 예제(`member`, `order`, `discount`)를 통해 DI·싱글톤·컴포넌트 스캔·빈 생명주기·빈 스코프를 다룬다.

- Gradle 그룹/아티팩트: `hello` / `core`, 버전 `0.0.1-SNAPSHOT`
- 베이스 패키지: `hello.core`
- 빌드 도구: Gradle 9.5.1 (wrapper 사용)

## 강의 실습 워크플로

**사용자가 강의를 보며 코드를 직접 작성하고, Claude는 검증·커밋·푸시를 담당한다.** 요청은 보통 `"<번호>. <강의 제목>" + "커밋, 푸시"` 형태로 들어온다.

- 커밋 메시지는 `<type>(core): <강의번호>. <강의 제목>` 형식을 유지한다(전역 지침의 Conventional Commits 준수).
- **작업 트리의 코드를 임의로 고치지 않는다.** 사용자가 이미 작성해 둔 상태를 그대로 커밋하는 것이 기본이다.
- **이전 강의 코드는 주석으로 보존한다.** 방식 비교가 학습 목적이므로 주석 처리된 코드나 미사용 import를 정리하지 않는다.
- **의도적으로 빌드가 깨진 상태를 커밋하기도 한다.** 문제 상황을 재현하는 강의(예: 48. 조회 빈이 2개 이상, 62. request 스코프 예제)가 있으며, 다음 강의에서 해결된다. 이때는 커밋 메시지 footer에 `BREAKING CHANGE:`로 실패 범위와 해결 시점을 남긴다.
- 커밋 전에 `./gradlew build`로 확인하고, 강의가 의도한 동작(콘솔 출력·예외 종류)까지 검증해 보고한다.

## 명령어

빌드·테스트는 항상 wrapper(`./gradlew`)로 실행한다. 저장소에 Gradle이 별도로 설치돼 있다고 가정하지 않는다.

```bash
./gradlew build              # 컴파일 + 테스트 + 패키징
./gradlew bootRun            # 애플리케이션 실행
./gradlew test               # 전체 테스트
./gradlew clean              # 빌드 산출물 삭제
```

단일 테스트 실행:

```bash
./gradlew test --tests 'hello.core.CoreApplicationTests'              # 클래스 단위
./gradlew test --tests 'hello.core.CoreApplicationTests.contextLoads' # 메서드 단위
./gradlew test --tests '*CoreApplication*'                            # 패턴 매칭
```

테스트 리포트는 `build/reports/tests/test/index.html`에 생성된다. 테스트가 `System.out`으로 찍는 출력(강의 예제가 동작 확인에 자주 쓴다)은 `build/test-results/test/TEST-<FQCN>.xml`의 `<system-out>`에서 확인한다.

웹 동작을 확인할 때는 IDE에서 이미 8080을 쓰고 있을 수 있으므로 포트를 바꿔 띄운다:

```bash
./gradlew bootRun --args='--server.port=18080'   # 백그라운드 실행 후 curl로 확인, 끝나면 종료
```

## 아키텍처·설정 제약

- **Java 25 toolchain** — `build.gradle`의 `java.toolchain.languageVersion`이 25로 고정돼 있다. Gradle이 JDK 25를 찾지 못하면 빌드가 실패하므로, 로컬 JDK 버전을 임의로 낮추지 말고 toolchain 설정을 유지한다. IntelliJ 쪽 설정(`.idea/compiler.xml`, `.idea/misc.xml`)도 bytecode target 25 / JDK_25로 맞춰져 있다.
- **의존성** — `spring-boot-starter`(core), `spring-boot-starter-webmvc`(웹 스코프 실습), `lombok`, `jakarta.inject-api:2.0.1`(JSR-330). 웹 스타터가 있으므로 `bootRun`은 내장 톰캣을 띄우고 8080에서 계속 실행된다.
- **Spring Boot 4 모듈 재편** — 웹 스타터 이름이 `spring-boot-starter-web`이 아니라 **`spring-boot-starter-webmvc`**다. 강의(Boot 3.x 기준) 코드와 이름이 다르므로 그대로 복사하면 의존성을 찾지 못한다.
- **lombok** — `compileOnly` + `annotationProcessor`(테스트용도 별도)로 등록돼 있고 `configurations { compileOnly { extendsFrom annotationProcessor } }`가 필요하다. IntelliJ에서는 annotation processing이 켜져 있어야 한다.
- **테스트 플랫폼은 JUnit 5(Jupiter)** — `tasks.named('test') { useJUnitPlatform() }`로 설정돼 있고, `junit-platform-launcher`가 `testRuntimeOnly`로 들어가 있다. JUnit 4 API(`org.junit.Test`)는 사용할 수 없다.
- **컴포넌트 스캔 범위** — `@SpringBootApplication`이 붙은 `CoreApplication`이 `hello.core` 패키지에 있으므로, 스캔 대상은 `hello.core` 및 그 하위 패키지다. 새 빈/설정 클래스는 이 하위에 둔다.
- **스캔 기반 테스트가 함께 깨진다** — `AutoAppConfig`가 `hello.core` 전체를 스캔하므로, `src/main`에 빈을 추가하면 `CoreApplicationTests`, `AutoAppConfigTest`, `AllBeanTest`가 동시에 영향을 받는다. 강의 본편은 `main` 실행 실패만 보여주지만 이 저장소에서는 테스트도 함께 실패하는 것이 정상이다.
- **애플리케이션 설정** — `src/main/resources/application.properties`에 `spring.application.name=core`만 있다. 설정 추가 시 이 파일을 사용한다(YAML 파일은 존재하지 않는다).

## 코드 스타일

인덴트가 파일 종류에 따라 다르다. **자바 소스는 4칸 스페이스**(강의 코드를 따라 작성한 것), **`build.gradle` 등 Gradle 설정은 탭**(Spring Initializr 기본값)이다. 편집 시 해당 파일의 기존 방식을 따른다.

`.idea/google-java-format.xml`에서 google-java-format은 **비활성화**돼 있으므로 자동 포매터를 켜서 전체 파일을 재정렬하지 않는다.

## 줄바꿈(EOL) 규칙

`.gitattributes`에 강제돼 있다: `gradlew`는 LF, `*.bat`은 CRLF, `*.jar`는 binary. 이 파일들을 편집할 때 줄바꿈을 바꾸지 않는다.
