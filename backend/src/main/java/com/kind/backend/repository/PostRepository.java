package com.kind.backend.repository;

import com.kind.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndStatus(Long id, String status);
    List<Post> findTop10ByStatusOrderByCreatedAtDesc(String status);

    @Query("""
    select p from Post p where p.status = 'ACTIVE'
    and (:departmentId IS NULL OR p.department.id = :departmentId)
    and (:type IS NULL OR p.type = :type)
    and(
                      :search IS NULL OR :search = ''
                      OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))
                      OR LOWER(p.body) LIKE LOWER(CONCAT('%', :search, '%'))
                 )
    order by p.createdAt desc 
""")
    List<Post> findFeed(
            @Param("departmentId") Long departmentId,
            @Param("type") String type,
            @Param("search") String search
    );
}
