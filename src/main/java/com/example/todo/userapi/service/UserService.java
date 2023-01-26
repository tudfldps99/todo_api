// 2023-01-26
package com.example.todo.userapi.service;

import com.example.todo.userapi.dto.UserSignUpDTO;
import com.example.todo.userapi.dto.UserSignUpResponseDTO;
import com.example.todo.userapi.entity.UserEntity;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;      // final 로 생성하면 @RequiredArgsConstructor 를 이용하여 자동으로 생성자 주입됨

    // 회원가입 처리
    public UserSignUpResponseDTO create(final UserSignUpDTO userSignUpDTO) {
        if (userSignUpDTO == null) {
            throw new RuntimeException("가입정보가 없습니다.");
        }
        final String email = userSignUpDTO.getEmail();
        if (userRepository.existsByEmail(email)) {      // 이메일 중복검사
            log.warn("Email already exists - {}", email);
            throw new RuntimeException("중복된 이메일입니다.");
        }

        // 패스워드 인코딩
        String rawPassword = userSignUpDTO.getPassword();   // 평문 패스워드
        String encodedPassword = passwordEncoder.encode(rawPassword);   // 암호화처리
        userSignUpDTO.setPassword(encodedPassword);     // 암호화된 패스워드 set

        // 회원가입
        UserEntity savedUser = userRepository.save(userSignUpDTO.toEntity());    // DTO 를 Entity 로 변환시킴

        // savedUser 를 그대로 return 하면 클라이언트에게 비밀번호 등 모두 보여주는 것이므로 UserSignUpResponseDTO 에서 dto 로 변환시켜서 return
        return new UserSignUpResponseDTO(savedUser);
    }
}