package my.auction.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailVerifyResponseDto {
	
	private boolean verified;
	
	private String message;
	
}
