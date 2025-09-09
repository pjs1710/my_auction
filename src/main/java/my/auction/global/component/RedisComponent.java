package my.auction.global.component;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisComponent {
	
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
}
