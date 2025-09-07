package my.auction.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import my.auction.global.util.RedisUtil;
import my.auction.user.domain.User;
import my.auction.user.dto.UserRegisterRequestDto;
import my.auction.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // 각 테스트 후 롤백 진행
class UserServiceImplTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RedisUtil redisUtil;
	
	private String testEmail = "test1234@naver.com";
	private String testPassword = "test1234";
	
	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}
	
	@Test
	@DisplayName("사용자 존재 여부 확인 - Success")
	void isUserExists_성공() {
		// given
		User testUser = User.builder()
			.email(testEmail)
			.password(testPassword)
			.build();
		userRepository.saveAndFlush(testUser);
		
		// when
		boolean result = userService.isUserExists(testEmail);
		
		// then
		assertThat(result).isTrue();
	}
	
	@Test
	@DisplayName("사용자 존재 여부 확인 - Failed")
	void isUserExists_실패() {
		// given
		User testUser = User.builder()
			.email(testEmail)
			.password(testPassword)
			.build();
		userRepository.saveAndFlush(testUser);
		String anotherTestEmail = "test4321@naver.com"; // 실패 테스트를 위한 다른 email
		
		// when
		boolean result = userService.isUserExists(anotherTestEmail);
		
		// then
		assertThat(result).isFalse();
	}
	
	@Test
	@DisplayName("이메일 인증 확인 - Success")
	void isEmailVerified_성공() {
		// given
		User testUser = User.builder()
			.email(testEmail)
			.password(testPassword)
			.email_verified_time(LocalDateTime.now())
			.build();
		userRepository.saveAndFlush(testUser);
		
		// when
		boolean result = userService.isEmailVerified(testEmail);
		
		// then
		assertThat(result).isTrue();
	}
	
	@Test
	@DisplayName("이메일 인증 확인 - Failed (1)")
	void isEmailVerified_실패_사용자_존재() {
		// given
		User testUser = User.builder()
			.email(testEmail)
			.password(testPassword)
			.build();
		userRepository.saveAndFlush(
			testUser); // 현재 이메일 인증은 안된 상태 : email_verified_time이 null이기 때문에 (Default Value임)
		
		// when
		boolean result = userService.isEmailVerified(testEmail);
		
		// then
		assertThat(result).isFalse();
	}
	
	@Test
	@DisplayName("이메일 인증 확인 - Failed (2)")
	void isEmailVerified_실패_사용자_미존재() {
		// given
		String notExistEmail = "notExistEmail@naver.com"; // 이메일 인증 코드가 전송되지 않았던(Redis에 저장되지 않은) Email이라 가정
		
		// when
		boolean result = userService.isEmailVerified(notExistEmail);
		
		// then
		assertThat(result).isFalse();
	}
	
	@Test
	@DisplayName("회원가입 - Success")
	void registerUser_성공() {
		// given
		redisUtil.setDataExpire(testEmail, "true", 5); // 인증된 걸로 임시 처리!
		
		UserRegisterRequestDto requestDto = UserRegisterRequestDto.builder()
			.email(testEmail)
			.password(testPassword)
			.build();
		
		// when
		userService.registerUser(requestDto);
		
		// then
		User registeredUser = userRepository.findByEmail(testEmail).orElse(null);
		assertThat(registeredUser).isNotNull(); // null이 아니면 제대로 가입된 것!
		assertThat(registeredUser.getEmail()).isEqualTo(testEmail);
	}
	
	@Test
	@DisplayName("회원가입 - Failed (1)")
	void registerUser_실패_이메일_중복() {
		// given
		User alreadyRegisterEmail = User.builder()
			.email(testEmail)
			.password(testPassword)
			.email_verified_time(LocalDateTime.now())
			.build();
		userRepository.saveAndFlush(alreadyRegisterEmail);
		
		UserRegisterRequestDto duplicatedRequestDto = UserRegisterRequestDto.builder()
			.email(testEmail)
			.password(testPassword)
			.build();
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> { // 중복 가입을 시도하면 IllegalArgumentException을 발생시키도록 했기 때문에, 해당 예외를 잡는지 확인
			userService.registerUser(duplicatedRequestDto);
		});
	}
	
	@Test
	@DisplayName("회원가입 - Failed (2)")
	void registerUser_실패_이메일_미인증() {
		// given
		UserRegisterRequestDto noVerifiedRequestDto = UserRegisterRequestDto.builder()
			.email(testEmail)
			.password(testPassword)
			.build();
		
		// when & then
		assertThrows(IllegalStateException.class, () -> { // 마찬가지로 인증이 안된 이메일의 회원가입 요청은 IllegalStateException을 발생시키도록 했기 때문에, 해당 예외를 잡는지 확인
			userService.registerUser(noVerifiedRequestDto);
		});
	}
}