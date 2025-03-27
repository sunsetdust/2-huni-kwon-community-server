package community.startupcode.api.User.Dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserDTO {

    @Data
    @Builder
    static public class UserSignUpDTO{
        private Long id;
        private String email;
        private String password;
        private String name;
        private String profileUrl;
    }

    @Data
    @Builder
    public static class LoginRequestDTO{
        private String email;
        private String password;
    }

    @Data
    @Builder
    public static class LoginResponseDTO {
        private Long id;
        private String email;
        private String name;
        private String accessToken;
    }

    @Data
    @Builder
    public static class UserResponseDTO {
        private Long id;
        private String email;
        private String name;
        private String profileImg;
    }

    @Data
    @Builder
    public static class UpdateUserRequestDTO{
        private String name;
        private String profileImg;
    }

    @Data
    @Builder
    public static class UpdatePasswordRequestDTO{
        private String oldPassword;
        private String newPassword;
    }
}
