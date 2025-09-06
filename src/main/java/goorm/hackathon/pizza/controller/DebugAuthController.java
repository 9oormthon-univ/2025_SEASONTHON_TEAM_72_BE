//// src/main/java/goorm/hackathon/pizza/controller/DebugAuthController.java
//package goorm.hackathon.pizza.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//
//@Profile("local")
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/_dev")
//public class DebugAuthController {
//
//    @GetMapping("/whoami")
//    public Map<String, Object> whoami(Authentication auth) {
//        Map<String, Object> m = new LinkedHashMap<>();
//        if (auth == null) {
//            m.put("authenticated", false);
//            return m;
//        }
//        m.put("authenticated", auth.isAuthenticated());
//        m.put("principal", auth.getPrincipal()); // 여기 값이 1001이어야 함
//        m.put("authorities", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
//        return m;
//    }
//}
