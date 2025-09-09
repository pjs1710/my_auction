package my.auction.email.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.auction.email.dto.MailSendRequestDto;
import my.auction.email.dto.MailVerifyRequestDto;
import my.auction.email.dto.MailVerifyResponseDto;
import my.auction.email.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class MailController {
	
	private final MailService mailService;
	
	/**
	 * 이메일 인증코드 전송 API
	 *
	 * @param requestDto
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> sendVerificationCode(
		@Valid @RequestBody MailSendRequestDto requestDto) {
		mailService.sendVerificationCode(requestDto.getEmail());
		
		return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
	}
	
	/**
	 * 이메일 인증코드 확인 API
	 *
	 * @param requestDto
	 * @return
	 */
	@PostMapping("/check")
	public ResponseEntity<?> isCodeValid(@Valid @RequestBody MailVerifyRequestDto requestDto) {
		mailService.verifyCode(requestDto.getEmail(), requestDto.getCode()); // 인증 유무 확인
		
		return ResponseEntity.ok(new MailVerifyResponseDto(true, "이메일 인증이 정상적으로 완료되었습니다!"));
	}
}
