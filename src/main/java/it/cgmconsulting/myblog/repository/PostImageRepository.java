package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.PostImage;
import it.cgmconsulting.myblog.entity.PostImageId;
import it.cgmconsulting.myblog.entity.common.ImagePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PostImageRepository extends JpaRepository<PostImage, PostImageId> {

    long countByPostImageIdPostIdAndImagePosition(long postId, ImagePosition imagePosition);

    void deleteAllByPostImageIdPostIdAndPostImageIdFilenameIn(long postId, Set<String> filesToDelete);

    List<PostImage> findByPostImageIdPostIdAndPostImageIdFilenameIn(long postid, Set<String> filesToDelete);

    @Query("SELECT p FROM PostImage p WHERE p.postImageId.post.id = :postId AND p.postImageId.filename IN :filesToDelete")
    List<PostImage> getPostImages (@Param("postId") long postId, @Param("filesToDelete") Set<String> filesToDelete);
}