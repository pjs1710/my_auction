package my.auction.email.service;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import my.auction.global.component.RedisComponent;
import my.auction.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class MailServiceImpl implements MailService {

	//* TODO : Custom Exception 클래스 구현하기
	
	private final JavaMailSender javaMailSender;
	private final RedisComponent redisComponent;
	private final UserService userService;
	
	@Value("${spring.mail.sender}")
	private String senderMail;
	
	private static final long EXPIRE_TIME = 5; // 5분
	private static final long VERIFIED_EXPIRE_TIME = 60; // 인증 완료 정보는 60분 유지
	
	@Override
	public void sendEmail(String to, String code) {
		String subject = "[My Auction] 실시간 경매 서비스 이메일 인증";
		String codeMessage = "인증번호는 : [" + code + "] 입니다. 타인에게 절대 알려주지 마세요.";
		String message = "안녕하세요 🙂\n\n 본인 인증을 위해 아래 인증번호를 입력해주세요.\n\n" + codeMessage;
		
		sendMessage(to, subject, message);
	}
	
	@Override
	public void sendVerificationCode(String email) {
		try {
			if (userService.isUserExists(email) && userService.isEmailVerified(email)) {
				throw new Exception("이미 인증된 이메일입니다. " + email);
			}
			
			String verificationCode = generateVerificationCode();
			
			redisComponent.setDataExpire(email, verificationCode, EXPIRE_TIME); // 만료시간 5분 설정
			sendEmail(email, verificationCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public boolean verifyCode(String email, String code) {
		try {
			if (email == null || code == null) {
				return false;
			}
			
			// 사용자가 실수로 공백을 입력할 수도 있음!
			// 공백을 제거해서 혹시 모를 오류를 방지하기
			String trimmedEmail = email.trim();
			String trimmedCode = code.trim();
			String redisKey = trimmedEmail;
			String storedCode = redisComponent.getData(redisKey);
			
			if (storedCode == null) {
				return false;
			}
			
			String trimmedStoredCode = storedCode.trim();
			if (trimmedStoredCode.equals(trimmedCode)) {
				redisComponent.deleteData(redisKey);
				String verifiedKey = trimmedEmail;
				redisComponent.setDataExpire(verifiedKey, "true", VERIFIED_EXPIRE_TIME);
				/*
				 - 인증 완료 표시로 true로 값을 세팅하기.
				 - 인증 후 60분동안 회원 가입을 마무리 해야됨.
				 */
				
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void sendMessage(String to, String subject, String message) {
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(senderMail);
			mailMessage.setTo(to);
			mailMessage.setSubject(subject);
			mailMessage.setText(message);
			javaMailSender.send(mailMessage);
		} catch (MailException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String generateVerificationCode() {
		Random random = new Random();
		int code = 100000 + random.nextInt(900000);
		return String.valueOf(code);
	}
}
