package me.yunhui.catalog.infrastructure.redis;

import me.yunhui.catalog.domain.repository.CatalogKeywordRepository;
import me.yunhui.catalog.domain.vo.CatalogKeyword;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class RedisCatalogKeywordRepository implements CatalogKeywordRepository {
    
    private static final String POPULAR_KEYWORDS_KEY = "popular_keywords";
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOps;
    
    public RedisCatalogKeywordRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOps = redisTemplate.opsForZSet();
    }
    
    @Override
    public void incrementSearchCount(CatalogKeyword keyword) {
        zSetOps.incrementScore(POPULAR_KEYWORDS_KEY, keyword.value(), 1.0);
    }
    
    @Override
    public List<CatalogKeyword> findTopPopularKeywords(int limit) {
        Set<ZSetOperations.TypedTuple<String>> topKeywords = zSetOps.reverseRangeWithScores(
            POPULAR_KEYWORDS_KEY, 0, limit - 1
        );
        
        if (topKeywords == null) {
            return List.of();
        }
        
        return topKeywords.stream()
                .map(tuple -> CatalogKeyword.included(tuple.getValue()))
                .toList();
    }
}