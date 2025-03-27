package community.startupcode.Repositories;

import community.startupcode.Model.Post;
import community.startupcode.Model.UserPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPostLikeRepository extends JpaRepository<UserPostLike, Long> {
    Optional<UserPostLike> findByUser_IdAndPost_Id(Long userId, Long postId);
    Long countByPostAndStateTrue(Post post);
}