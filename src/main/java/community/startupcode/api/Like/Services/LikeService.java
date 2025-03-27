package community.startupcode.api.Like.Services;

import community.startupcode.Model.Post;
import community.startupcode.Model.User;
import community.startupcode.Model.UserPostLike;
import community.startupcode.Repositories.PostRepository;
import community.startupcode.Repositories.UserPostLikeRepository;
import community.startupcode.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LikeService {

    private final UserPostLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    //좋아요 레코드가 없으면 생성, 이미 존재하면 state가 false인 경우 true로 업데이트(반대도 가능)
    public void updateLike(Long userId, Long postId) {
        // 사용자와 게시글 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Optional<UserPostLike> optionalLike = likeRepository.findByUser_IdAndPost_Id(userId, postId);
        if (optionalLike.isPresent()) {
            UserPostLike existingLike = optionalLike.get();
            // 현재 상태가 true면 false, 아니면 true로
            existingLike.setState(!Boolean.TRUE.equals(existingLike.getState()));
            existingLike.setUpdatedAt(LocalDateTime.now());
            likeRepository.save(existingLike);
        } else {
            // 새로운 좋아요 생성
            UserPostLike newLike = UserPostLike.builder()
                    .user(user)
                    .post(post)
                    .state(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            likeRepository.save(newLike);
        }
        // 게시글의 활성 좋아요 수 업데이트
        updatePostLikes(post);
    }


    // 게시글의 likes 카운트 업데이트
    private void updatePostLikes(Post post) {
        Long likeCount = likeRepository.countByPostAndStateTrue(post);
        post.setLikes(likeCount);
        postRepository.save(post);
    }

}
