package community.startupcode.api.Post.Services;

import community.startupcode.Model.Post;
import community.startupcode.Model.User;
import community.startupcode.Repositories.PostRepository;
import community.startupcode.Repositories.UserRepository;
import community.startupcode.api.Post.Dto.PostDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 생성
    public PostDTO.PostResponseDTO createPost(Long userId, PostDTO.PostRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .contentUrl(request.getContentUrl())
                .views(0L)
                .likes(0L)
                .comments(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepository.save(post);

        PostDTO.PostResponseDTO response = PostDTO.PostResponseDTO.builder()
                .id(post.getId())
                .userId(post.getUser().getId()) // user 객체의 ID만 추출
                .title(post.getTitle())
                .content(post.getContent())
                .contentUrl(post.getContentUrl())
                .views(post.getViews())
                .likes(post.getLikes())
                .comments(post.getComments())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();

        return response;
    }


    //특정 게시글 조회
    public PostDTO.PostResponseDTO getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.setViews(post.getViews() + 1);
        postRepository.save(post); // 변경사항 반영
        return convertToPost(post);
    }

    // 전체 게시글 목록 조회
    public List<PostDTO.PostResponseDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(this::convertToPost)
                .collect(Collectors.toList());
    }

    // 게시글 수정
    public PostDTO.PostResponseDTO updatePost(Long userId, Long postId, PostDTO.PostRequestDTO request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 검증
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("게시글 수정 권한이 없습니다.");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setContentUrl(request.getContentUrl());
        post.setUpdatedAt(LocalDateTime.now());
        Post updatedPost = postRepository.save(post);
        return convertToPost(updatedPost);
    }

    // 게시글 삭제
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("게시글 삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    private PostDTO.PostResponseDTO convertToPost(Post post) {
        return PostDTO.PostResponseDTO.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .title(post.getTitle())
                .content(post.getContent())
                .contentUrl(post.getContentUrl())
                .views(post.getViews())
                .likes(post.getLikes())
                .comments(post.getComments())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
