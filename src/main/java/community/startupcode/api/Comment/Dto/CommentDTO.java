package community.startupcode.api.Comment.Dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    @Data
    @Builder
    public static class CommentResponseDTO{
        private Long id;
        private Long userId;
        private String userName;
        private String userProfileUrl;
        private Long postId;
        private String comment;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    public static class CommentCreateRequestDTO {
        private String comment;
    }

    @Data
    @Builder
    public static class CommentUpdateRequestDTO {
        private String comment;
    }
}