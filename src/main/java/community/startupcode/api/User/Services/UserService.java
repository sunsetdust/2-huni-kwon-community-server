package community.startupcode.api.User.Services;

import community.startupcode.Model.User;
import community.startupcode.Repositories.UserRepository;
import community.startupcode.api.User.Dto.UserDTO;
import community.startupcode.security.JWTProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private JWTProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;    //비밀번호 암호화
    public User signupUser(UserDTO.UserSignUpDTO signUpDTO){
        // 이메일 중복 체크
        if (userRepository.findByEmail(signUpDTO.getEmail()).isPresent()) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }

        // 비밀번호 암호화 적용
        String encryptedPassword = passwordEncoder.encode(signUpDTO.getPassword());

        User user = User.builder()
                .email(signUpDTO.getEmail())
                .password(encryptedPassword)  // 실제 운영에서는 암호화를 적용하세요.
                .name(signUpDTO.getName())
                .profileUrl(signUpDTO.getProfileUrl()) // DTO의 필드명이 profileImg로 되어 있으므로 매핑
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public UserDTO.LoginResponseDTO LoginUser(UserDTO.LoginRequestDTO loginRequestDTO){
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        // BCrypt 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(user);

        return UserDTO.LoginResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(accessToken)
                .build();
    }

    //유저 정보 가져오는 로직
    public UserDTO.UserResponseDTO GetUserInfo(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        return UserDTO.UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImg(user.getProfileUrl())
                .build();
    }

    // 유저 정보 수정 로직 (이름, 프로필 이미지 수정)
    public UserDTO.UserResponseDTO updateUserInfo(Long id, UserDTO.UpdateUserRequestDTO updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        user.setName(updateDto.getName());
        user.setProfileUrl(updateDto.getProfileImg());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return UserDTO.UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImg(user.getProfileUrl())
                .build();
    }

    // 비밀번호 변경 로직
    public void changePassword(Long id, UserDTO.UpdatePasswordRequestDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        // 기존 비밀번호 확인
        if (!user.getPassword().equals(updateDTO.getOldPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(updateDTO.getNewPassword());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // 회원탈퇴 로직
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

}
