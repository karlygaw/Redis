package kz.narxoz.redis.dist01redis.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cache")
public class CacheController {

    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/monitor")
    public Map<String, Long> monitorCache() {
        return redisTemplate.keys("*").stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> redisTemplate.getExpire(key, TimeUnit.SECONDS)
                ));
    }
}
