package community.startupcode.api.User.Controller;

import community.startupcode.Model.User;
import community.startupcode.api.User.Dto.UserDTO;
import community.startupcode.api.User.Services.UserService;
import community.startupcode.common.ApiResponse;
import community.startupcode.security.JWTProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JWTProvider jwtTokenProvider;

//    public UserController(JWTProvider jwtTokenProvider) {
//        this.jwtTokenProvider = jwtTokenProvider;
//    }

    @PostMapping("")
    public ApiResponse<User> CreateUserController(@RequestBody UserDTO.UserSignUpDTO signUpDTO){
        User userdata = userService.signupUser(signUpDTO);
        return new ApiResponse<>(userdata,"200");
    }

    @PostMapping("/login")
    public ApiResponse<UserDTO.LoginResponseDTO> Login(@RequestBody UserDTO.LoginRequestDTO loginRequestDTO){
        UserDTO.LoginResponseDTO response = userService.LoginUser(loginRequestDTO);
        return new ApiResponse<>(response, "200");
    }

    @GetMapping("")
    public ApiResponse<UserDTO.UserResponseDTO> GetUserController(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("유효한 토큰이 존재하지 않습니다.");
        }
        String token = header.substring(7);

        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 토큰에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);

        // 사용자 정보 조회
        UserDTO.UserResponseDTO response = userService.GetUserInfo(userId);
        return new ApiResponse<>(response, "200");
    }

    //유저 정보 수정 컨트롤러
    @PutMapping("")
    public ApiResponse<UserDTO.UserResponseDTO> UpdateUserInfo(HttpServletRequest request, @RequestBody UserDTO.UpdateUserRequestDTO updateUserRequestDTO){

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("유효한 토큰이 존재하지 않습니다.");
        }

        String token = header.substring(7);
        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 토큰에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);

        UserDTO.UserResponseDTO response = userService.updateUserInfo(userId, updateUserRequestDTO);
        return new ApiResponse<>(response, "200");
    }
    
    //비밀번호 변경 컨트롤러
    @PutMapping("/password")
    public ApiResponse<String> ChangePassword(
            HttpServletRequest request,
            @RequestBody UserDTO.UpdatePasswordRequestDTO updatePasswordRequestDTO){

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("유효한 토큰이 존재하지 않습니다.");
        }

        String token = header.substring(7);
        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 토큰에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);

        userService.changePassword(userId, updatePasswordRequestDTO);
        return new ApiResponse<>("비밀번호가 변경되었습니다.", "200");
    }

    //회원탈퇴 컨트롤러
    @DeleteMapping("")
    public ApiResponse<String> DeleteUser(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("유효한 토큰이 존재하지 않습니다.");
        }

        String token = header.substring(7);
        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 토큰에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);

        userService.deleteUser(userId);

        return new ApiResponse<>("회원탈퇴 성공.", "200");
    }
}
