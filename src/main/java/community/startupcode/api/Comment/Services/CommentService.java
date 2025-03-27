package community.startupcode.api.Comment.Services;

import community.startupcode.Model.Comment;
import community.startupcode.Model.Post;
import community.startupcode.Model.User;
import community.startupcode.Repositories.CommentRepository;
import community.startupcode.Repositories.PostRepository;
import community.startupcode.Repositories.UserRepository;
import community.startupcode.api.Comment.Dto.CommentDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //댓글 get
    public List<CommentDTO.CommentResponseDTO> getCommentsByPost(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .map(comment -> CommentDTO.CommentResponseDTO.builder()
                        .id(comment.getId())
                        .userId(comment.getUser().getId())
                        .userName(comment.getUser().getName())
                        .userProfileUrl(comment.getUser().getProfileUrl())
                        .postId(comment.getPost().getId())
                        .comment(comment.getComment())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 댓글 생성
    public CommentDTO.CommentResponseDTO createComment(Long postId, Long userId, CommentDTO.CommentCreateRequestDTO request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        // 댓글 생성 후, 게시글의 comments 카운트 증가
        post.setComments(post.getComments() + 1);
        postRepository.save(post);
        
        return CommentDTO.CommentResponseDTO.builder()
                .id(comment.getId())
                .userId(user.getId())
                .userName(user.getName())
                .userProfileUrl(user.getProfileUrl())
                .postId(postId)
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    // 댓글 수정
    public CommentDTO.CommentResponseDTO updateComment(Long commentId,Long postId, Long userId, CommentDTO.CommentUpdateRequestDTO request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 작성자 검증
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 수정 권한이 없습니다.");
        }

        //게시물 검증
        // 작성자 검증
        if (!comment.getPost().getId().equals(postId)) {
            throw new RuntimeException("해당 게시글이 아닙니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        comment.setComment(request.getComment());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        return CommentDTO.CommentResponseDTO.builder()
                .id(comment.getId())
                .userId(user.getId())
                .userName(user.getName())
                .userProfileUrl(user.getProfileUrl())
                .postId(postId)
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    // 댓글 삭제
    public void deleteComment(Long postId, Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 작성자 검증
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }
        
        //게시글의 댓글 수 감소
        Post post = comment.getPost();
        if(post.getComments() > 0) {
            post.setComments(post.getComments() - 1);
            postRepository.save(post);
        }

        commentRepository.delete(comment);
    }
}
