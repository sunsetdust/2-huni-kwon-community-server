package community.startupcode.api.Post.Dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {

    @Data
    @Builder
    public static class PostResponseDTO{
        private Long id;
        private Long userId;
        private String title;
        private String content;
        private String contentUrl;
        private Long views;
        private Long likes;
        private Long comments;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    public static class PostRequestDTO{
        private String title;
        private String content;
        private String contentUrl;
    }


}
