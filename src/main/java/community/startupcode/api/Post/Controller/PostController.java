package community.startupcode.api.Post.Controller;

import community.startupcode.Model.Post;
import community.startupcode.api.Comment.Dto.CommentDTO;
import community.startupcode.api.Comment.Services.CommentService;
import community.startupcode.api.Like.Services.LikeService;
import community.startupcode.api.Post.Dto.PostDTO;
import community.startupcode.api.Post.Services.PostService;
import community.startupcode.common.ApiResponse;
import community.startupcode.security.JWTProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final JWTProvider jwtTokenProvider;
    private final CommentService commentService;
    private final LikeService likeService;

    @PostMapping("")
    public ApiResponse<PostDTO.PostResponseDTO> CreatePost(HttpServletRequest request, @RequestBody PostDTO.PostRequestDTO postRequestDTO){
        Long userId = getUserIdFromRequest(request);
        PostDTO.PostResponseDTO response = postService.createPost(userId, postRequestDTO);

        return new ApiResponse<>(response, "200");
    }

    // 전체 게시글 조회 API
    @GetMapping("")
    public ApiResponse<List<PostDTO.PostResponseDTO>> getAllPosts(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("유효한 토큰이 존재하지 않습니다.");
        }
        String token = header.substring(7);
        if(!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        List<PostDTO.PostResponseDTO> responses = postService.getAllPosts();
        return new ApiResponse<>(responses, "200");
    }

    //특정 게시물 조회
    @GetMapping("/{postId}")
    public ApiResponse<PostDTO.PostResponseDTO> getPost(HttpServletRequest request, @PathVariable("postId") Long postId) {
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("유효한 토큰이 존재하지 않습니다.");
        }
        String token = header.substring(7);
        if(!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        PostDTO.PostResponseDTO response = postService.getPost(postId);
        return new ApiResponse<>(response, "200");
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ApiResponse<PostDTO.PostResponseDTO> updatePost(
            HttpServletRequest request,
            @PathVariable("postId") Long postId,
            @RequestBody PostDTO.PostRequestDTO updateRequest) {

        Long userId = getUserIdFromRequest(request);
        PostDTO.PostResponseDTO response = postService.updatePost(userId, postId, updateRequest);

        return new ApiResponse<>(response, "200");
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ApiResponse<String> deletePost(
            HttpServletRequest request,
            @PathVariable("postId") Long postId) {

        Long userId = getUserIdFromRequest(request);
        postService.deletePost(userId, postId);

        return new ApiResponse<>("게시글이 삭제되었습니다.", "200");
    }

    //댓글 목록 불러오기
    @GetMapping("{postId}/comments")
    public ApiResponse<List<CommentDTO.CommentResponseDTO>> getComments(HttpServletRequest request, @PathVariable Long postId){

        //로그인 되어 있는지 검증
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("유효한 토큰이 존재하지 않습니다.");
        }
        String token = header.substring(7);
        if(!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        List<CommentDTO.CommentResponseDTO> comments = commentService.getCommentsByPost(postId);
        return new ApiResponse<>(comments, "200");
    }

    //댓글 생성
    @PostMapping("/{postId}/comment")
    private ApiResponse<CommentDTO.CommentResponseDTO> createComment(
            HttpServletRequest request,
            @RequestBody CommentDTO.CommentCreateRequestDTO requestDTO,
            @PathVariable Long postId){

        Long userId = getUserIdFromRequest(request);
        CommentDTO.CommentResponseDTO response = commentService.createComment(postId, userId, requestDTO);

        return new ApiResponse<>(response, "200");
    }

    //댓글 수정
    @PutMapping("/{postId}/comment/{commentId}")
    private ApiResponse<CommentDTO.CommentResponseDTO> updateComment(
            HttpServletRequest request,
            @RequestBody CommentDTO.CommentUpdateRequestDTO requestDTO,
            @PathVariable Long postId,
            @PathVariable Long commentId){

        Long userId = getUserIdFromRequest(request);
        CommentDTO.CommentResponseDTO response = commentService.updateComment(commentId, postId , userId, requestDTO);

        return new ApiResponse<>(response, "200");
    }

    //댓글 삭제
    @DeleteMapping("/{postId}/comment/{commentId}")
    public ApiResponse<String> deleteComment(
            HttpServletRequest request,
            @PathVariable Long postId,
            @PathVariable Long commentId){

        Long userId = getUserIdFromRequest(request);
        commentService.deleteComment(postId, commentId,userId);

        return new ApiResponse<>("댓글이 삭제되었습니다.", "200");
    }

    // 좋아요 토글 API (POST /posts/{postId}/like)
    @PostMapping("/{postId}/like")
    public ApiResponse<String> updateLike(HttpServletRequest request, @PathVariable("postId") Long postId) {
        Long userId = getUserIdFromRequest(request);
        likeService.updateLike(userId, postId);
        return new ApiResponse<>("좋아요 처리 성공", "200");
    }


    // JWT 토큰에서 현재 사용자 ID 추출 메서드
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("유효한 토큰이 존재하지 않습니다.");
        }
        String token = header.substring(7);
        if(!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        return jwtTokenProvider.getUserIdFromJWT(token);
    }
}
