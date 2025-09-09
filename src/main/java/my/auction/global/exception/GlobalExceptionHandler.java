package my.auction.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(RestApiException.class)
	public ResponseEntity<ErrorResponse> handleRestApiException(RestApiException e) {
		ErrorCode errorcode = e.getErrorcode();
		log.error("RestApiException 발생 : {} - {}", errorcode.name(), errorcode.getMessage());
		
		return ResponseEntity
			.status(errorcode.getHttpStatus())
			.body(ErrorResponse.builder()
				.code(errorcode.name())
				.message(errorcode.getMessage())
				.build());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
		log.error("Unhandled Exception 발생", e);
		
		return ResponseEntity
			.status(CommonErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
			.body(ErrorResponse.builder()
				.code(CommonErrorCode.INTERNAL_SERVER_ERROR.name())
				.message(CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage())
				.build());
	}
}
