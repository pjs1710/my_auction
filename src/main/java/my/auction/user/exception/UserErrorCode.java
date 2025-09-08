package my.auction.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import my.auction.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
	
	EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
	EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
	;
	
	private final HttpStatus httpStatus;
	private final String message;
}
