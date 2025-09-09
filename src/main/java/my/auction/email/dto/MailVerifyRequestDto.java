package my.auction.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailVerifyRequestDto {
	
	@NotBlank(message = "이메일 입력은 필수입니다.")
	@Email(message = "올바른 이메일 형식을 입력해주세요.")
	private String email;
	
	@NotBlank(message = "인증 코드를 입력해주세요.")
	private String code;
}
