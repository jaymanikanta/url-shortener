package com.watermelon.urlshortener;


import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/rest/url")
public class UrlShortenerController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @PostMapping
    public String createUrl(@RequestBody String url){
        UrlValidator urlValidator = new UrlValidator(
                new String[]{"http", "https"}
        );
        if (urlValidator.isValid(url)) {
           String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
           System.out.println("ID generated: " + id);
           redisTemplate.opsForValue().set(id, url);
           return id;
        }
        throw new RuntimeException("Invalid URL!");
    }

    @GetMapping("/{id}")
    public String getUrl(@PathVariable String id){
        String url = redisTemplate.opsForValue().get(id);
        System.out.println("Url retrieved: " + url);
        return url;
    }
}
