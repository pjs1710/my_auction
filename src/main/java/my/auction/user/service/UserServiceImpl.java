package my.auction.user.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import my.auction.email.service.MailService;
import my.auction.global.exception.RestApiException;
import my.auction.user.domain.User;
import my.auction.user.dto.UserRegisterRequestDto;
import my.auction.user.exception.UserErrorCode;
import my.auction.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	
	@Override
	@Transactional(readOnly = true)
	public boolean isUserExists(String email) {
		return userRepository.existsByEmail(email);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isEmailVerified(String email) {
		/*
		사용자가 존재하면 Optional<User> 반환!
		또한 사용자가 존재하고, email_verified_time 컬럼이 null이 아니면 true!
		즉, 해당 이메일을 가진 사용자가 존재하는지와 해당 이메일의 인증이 완료되었는지 확인하기 위해서
		
		➡️ 인증이 완료되었다면 이메일 인증이 필요하지 않은 사용자임
		 */
		
		return userRepository.findByEmail(email)
			.map(user -> user.getEmailVerifiedTime() != null)
			.orElse(false);
	}
	
	@Override
	public void registerUser(UserRegisterRequestDto requestDto) {
		String email = requestDto.getEmail();
		
		// 요청 DTO에 입력된 email에 대한 검증부분
		// 이미 DB에 등록된 email의 사용자라면 회원가입 X
		// 또는 이메일 인증을 완료하지 않은 사용자라면 회원가입 X
		if (userRepository.existsByEmail(email)) {
			throw new RestApiException(UserErrorCode.EMAIL_ALREADY_EXISTS);
		}
		if (!mailService.isEmailVerified(email)) {
			throw new RestApiException(UserErrorCode.EMAIL_NOT_VERIFIED);
		}
		
		// --- 회원가입 대상자에 한해 DB에 등록
		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
		User user = User.builder()
			.email(email)
			.password(encodedPassword)
			.emailVerifiedTime(LocalDateTime.now()) // 인증이 최종적으로 완료된 후, 회원 등록 시각을 저장해놓기!
			.build();
		userRepository.save(user);
		mailService.clearEmailVerification(email);
	}
}
