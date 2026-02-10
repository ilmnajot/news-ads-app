package uz.ilmnajot.newsadsapp.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RedisConfig {

        @Bean
        // redisTemplate
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);

                // Key serializer (String)
                template.setKeySerializer(new StringRedisSerializer());
                template.setHashKeySerializer(new StringRedisSerializer());

                // Value serializer (JSON)
                template.setValueSerializer(jsonRedisSerializer());
                template.setHashValueSerializer(jsonRedisSerializer());

                template.afterPropertiesSet();
                return template;
        }

        @Bean
        // cacheManager
        public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

                // Default configuration
                RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(60)) // Default: 60 seconds
                                .serializeKeysWith(
                                                RedisSerializationContext.SerializationPair.fromSerializer(
                                                                new StringRedisSerializer()))
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair.fromSerializer(
                                                                jsonRedisSerializer()))
                                .disableCachingNullValues();

                // Custom TTL per cache name
                Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

                // News list: 60 seconds
                cacheConfigurations.put("newsList", defaultConfig.entryTtl(Duration.ofSeconds(60)));

                // News detail: 120 seconds
                cacheConfigurations.put("newsDetail", defaultConfig.entryTtl(Duration.ofSeconds(120)));

                // Categories: 5 minutes
                cacheConfigurations.put("categories", defaultConfig.entryTtl(Duration.ofMinutes(5)));

                // Tags: 10 minutes
                cacheConfigurations.put("tags", defaultConfig.entryTtl(Duration.ofMinutes(10)));
                // Ads cache -
                cacheConfigurations.put("publicAds", defaultConfig.entryTtl(Duration.ofSeconds(30)));

                return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(defaultConfig)
                                .withInitialCacheConfigurations(cacheConfigurations)
                                .build();
        }

        // jsonRedisSerializer
        private GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                // Use a more robust subtype validator
                BasicPolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                                .allowIfBaseType(Object.class)
                                .build();

                objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL,
                                JsonTypeInfo.As.PROPERTY);

                return new GenericJackson2JsonRedisSerializer(objectMapper);
        }
}
