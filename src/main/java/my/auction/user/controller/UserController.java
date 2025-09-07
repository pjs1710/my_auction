package my.auction.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.auction.user.dto.UserRegisterRequestDto;
import my.auction.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	/**
	 * 회원가입 API
	 *
	 * @param requestDto
	 * @return
	 */
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterRequestDto requestDto) {
		userService.registerUser(requestDto);
		
		return ResponseEntity.ok("회원가입이 완료되었습니다.");
	}
}
