package my.auction.email.service;

public interface MailService {
	
	void sendEmail(String to, String code);
	void sendVerificationCode(String email);
	boolean verifyCode(String email, String code);
	boolean isEmailVerified(String email);
	void clearEmailVerification(String email);
}
