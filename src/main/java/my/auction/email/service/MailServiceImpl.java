package my.auction.email.service;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.auction.email.exception.EmailErrorCode;
import my.auction.global.component.RedisComponent;
import my.auction.global.exception.RestApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailServiceImpl implements MailService {
	
	private final JavaMailSender javaMailSender;
	private final RedisComponent redisComponent;
	
	@Value("${spring.mail.sender}")
	private String senderMail;
	
	private static final long EXPIRE_TIME = 5; // 5분
	private static final long VERIFIED_EXPIRE_TIME = 60; // 인증 완료 정보는 60분 유지
	private static final String VERIFICATION_CODE_PREFIX = "Verification_Code:"; // 인증 코드 전송됐을 때의 Key Prefix
	private static final String VERIFIED_EMAIL_PREFIX = "Verified_Email:"; // 인증 완료됐을 때의 Key Prefix
	
	@Override
	public void sendEmail(String to, String code) {
		String subject = "[My Auction] 실시간 경매 서비스 이메일 인증";
		String codeMessage = "인증번호는 : [" + code + "] 입니다. 타인에게 절대 알려주지 마세요.";
		String message = "안녕하세요 🙂\n\n 본인 인증을 위해 아래 인증번호를 입력해주세요.\n\n" + codeMessage;
		
		sendMessage(to, subject, message);
	}
	
	@Override
	public void sendVerificationCode(String email) {
		String verificationCode = generateVerificationCode();
			
		redisComponent.setDataExpire(VERIFICATION_CODE_PREFIX + email, verificationCode, EXPIRE_TIME); // 만료시간 5분 설정
		sendEmail(email, verificationCode);
	}
	
	@Override
	public boolean verifyCode(String email, String code) {
		String storedCode = redisComponent.getData(VERIFICATION_CODE_PREFIX + email);
		
		if (storedCode == null) {
			throw new RestApiException(EmailErrorCode.CODE_EXPIRED);
		}
		if (!storedCode.equals(code)) {
			throw new RestApiException(EmailErrorCode.CODE_MISMATCH);
		}
		
		// 인증 성공 처리
		redisComponent.deleteData(VERIFICATION_CODE_PREFIX + email);
		redisComponent.setDataExpire(VERIFIED_EMAIL_PREFIX + email, "true", VERIFIED_EXPIRE_TIME);
		return true;
	}
	
	@Override
	public boolean isEmailVerified(String email) {
		String result = redisComponent.getData(VERIFIED_EMAIL_PREFIX + email);
		return "true".equals(result);
	}
	
	@Override
	public void clearEmailVerification(String email) {
		redisComponent.deleteData(VERIFIED_EMAIL_PREFIX + email);
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
			throw new RestApiException(EmailErrorCode.EMAIL_SEND_FAILED);
		}
	}
	
	private String generateVerificationCode() {
		Random random = new Random();
		int code = 100000 + random.nextInt(900000);
		return String.valueOf(code);
	}
}
