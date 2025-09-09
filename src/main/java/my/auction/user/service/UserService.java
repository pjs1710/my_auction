package my.auction.user.service;

import my.auction.user.dto.UserRegisterRequestDto;

public interface UserService {
	
	boolean isUserExists(String email);
	
	boolean isEmailVerified(String email);
	
	void registerUser(UserRegisterRequestDto requestDto);
}
