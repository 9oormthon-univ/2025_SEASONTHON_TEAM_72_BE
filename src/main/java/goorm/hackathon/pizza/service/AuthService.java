package goorm.hackathon.pizza.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import goorm.hackathon.pizza.dto.request.LoginRequest;
import goorm.hackathon.pizza.dto.request.SignupRequest;
import goorm.hackathon.pizza.dto.response.AuthResponse;
import goorm.hackathon.pizza.dto.response.SignupResponse;
import goorm.hackathon.pizza.entity.User;
import goorm.hackathon.pizza.jwt.JwtTokenProvider;
import goorm.hackathon.pizza.repository.UserRepository;
import goorm.hackathon.pizza.security.FirebaseAuthRestClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FirebaseAuthRestClient firebaseRest;

    public AuthService(UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider,
                       FirebaseAuthRestClient firebaseRest) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.firebaseRest = firebaseRest;
    }

    @Transactional
    public SignupResponse signup(SignupRequest req) {
        // 이메일 중복 체크 (DB)
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");
        }

        // Firebase 계정 생성
        String uid;
        try {
            UserRecord.CreateRequest create = new UserRecord.CreateRequest()
                    .setEmail(req.getEmail())
                    .setPassword(req.getPassword());
            uid = FirebaseAuth.getInstance().createUser(create).getUid();
        } catch (FirebaseAuthException e) {
            if ("EMAIL_EXISTS".equals(e.getErrorCode())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS(Firebase)");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "FIREBASE_SIGNUP_FAILED: " + e.getErrorCode());
        }

        // DB 저장 (닉네임은 중복 허용)
        User user = new User();
        user.setEmail(req.getEmail());
        user.setNickname(req.getNickname());
        user.setFirebaseUid(uid);
        user.setProvider("FIREBASE");
        user.setRole("USER");
        userRepository.save(user);

        // JWT 발급
        String token = jwtTokenProvider.createToken(user.getUserId(), user.getRole());
        return new SignupResponse(user.getUserId(), user.getEmail(), user.getNickname(), user.getFirebaseUid(), token);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        // Firebase 비번 로그인
        var signIn = firebaseRest.signInWithPassword(req.getEmail(), req.getPassword());

        // DB에 유저 없으면 새로 생성 (닉네임 = 이메일 앞부분)
        User user = userRepository.findByEmail(req.getEmail()).orElseGet(() -> {
            User u = new User();
            u.setEmail(req.getEmail());
            u.setNickname(defaultNickname(req.getEmail()));
            u.setFirebaseUid(signIn.getLocalId());
            u.setProvider("FIREBASE");
            u.setRole("USER");
            return userRepository.save(u);
        });

        // JWT 발급
        String token = jwtTokenProvider.createToken(user.getUserId(), user.getRole());
        return new AuthResponse(user.getUserId(), user.getEmail(), user.getNickname(), token);
    }

    // 이메일 앞부분을 닉네임으로 사용
    private String defaultNickname(String email) {
        String left = email.split("@")[0];
        return left.length() > 50 ? left.substring(0, 50) : left;
    }
}
