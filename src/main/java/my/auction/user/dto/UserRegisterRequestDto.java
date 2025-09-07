package my.auction.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequestDto {
	
	@NotBlank(message = "이메일 입력은 필수입니다.")
	@Email(message = "올바른 이메일 형식을 입력해주세요.")
	private String email;
	
	@NotBlank(message = "비밀번호 입력은 필수입니다.")
	@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
	private String password;
}
