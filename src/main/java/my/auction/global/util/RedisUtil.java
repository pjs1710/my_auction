package my.auction.global.util;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {
	
	private final StringRedisTemplate stringRedisTemplate;
	
	// Key 값을 통해 value 값 가져오기
	public String getData(String key) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		return valueOperations.get(key);
	}
	
//	// Key - Value 세팅하기
//	public void setData(String key, String value) {
//		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
//		valueOperations.set(key, value);
//	}
	
	// 만료시간 설정하기 ➡️ 기획은 5분으로 설정!
	public void setDataExpire(String key, String value, long expireTime) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		Duration duration = Duration.ofMinutes(expireTime);
		valueOperations.set(key, value, duration);
	}
	
	// Key 값으로 데이터 쌍 지우기
	public void deleteData(String key) {
		stringRedisTemplate.delete(key);
	}
	
	// 이메일 인증 완료 여부 확인 (회원가입 시 사용)
	public boolean isEmailVerified(String email) {
		String verifiedKey = email;
		String verified = getData(verifiedKey);
		return "true".equals(verified);
	}
	
	// 이메일 인증 완료 정보 삭제 (회원가입 완료 후 사용)
	public void clearEmailVerification(String email) {
		String verifiedKey = email;
		deleteData(verifiedKey);
	}
}
