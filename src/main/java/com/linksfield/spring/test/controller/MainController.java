package com.linksfield.spring.test.controller;

import com.linksfield.spring.test.util.DateUtil;
import com.linksfield.spring.test.util.IPv4Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.management.ObjectName;
import java.net.Inet4Address;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lmy
 * @date 2022/2/15 15:32
 * @desc Main
 */
@Controller
@Slf4j
public class MainController {
    private final AtomicLong count = new AtomicLong(0);

    private Map<String, Object> getServerInfo() {
        Map<String, Object> map = new HashMap<>(3);
        String ip = "localhost";
        try {
            Optional<Inet4Address> localIp4Address = IPv4Util.getLocalIp4Address();
            if (localIp4Address.isPresent()) {
                ip = localIp4Address.get().getHostAddress();
            }
        } catch (SocketException e) {
            log.error("get ip error {}", e.getMessage());
        }
        map.put("ip", ip);
        map.put("time", DateUtil.getTodayToSecond());
        map.put("count", count.getAndAdd(1L));
        return map;
    }


    @GetMapping
    public String index(Model model) {
        model.addAllAttributes(this.getServerInfo());
        log.info("index {}", model.asMap());
        return "index";
    }

    @GetMapping("demo")
    public String demo(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> result = restTemplate.getForObject("http://spring-demo:8082/api?message=fromTest", Map.class);
        log.info("demo rest result is {}", result);
        if (result != null) {
            model.addAllAttributes(result);
        }
        log.info("demo {}", model.asMap());
        return "demo";
    }

    @GetMapping("api")
    @ResponseBody
    public Object api(@RequestParam String message) {
        log.info("api {} {}", message, DateUtil.getTodayToSecond());
        Map<String, Object> map = this.getServerInfo();
        map.put("message", message);
        return map;
    }
}
