package team4.admin.board.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository extends JpaRepository<Post, Integer> {  
	 Page<Post> findByUser_Username(String username, Pageable pageable);
	    
	    // 根據用戶 ID 查詢貼文並支援分頁
	    Page<Post> findByUser_Id(Integer userId, Pageable pageable);
	    
	    // 查詢用戶的貼文總數
	    long countByUser_Id(Integer userId);
	    
	    // 若需要不分頁的查詢，保留原本的方法
	    List<Post> findByUser_Username(String username);
	    List<Post> findByUser_Id(int userId);
	    @Query("SELECT p FROM Post p WHERE p.visible = true AND p.reviewStatus != '已下架'")
	    Page<Post> findByVisibleTrue(Pageable pageable);
	    
	    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
	    Page<Post> findAllForAdmin(Pageable pageable);
	    
	    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND p.visible = true AND p.reviewStatus != '已下架' ")
	    Page<Post> findVisibleByUser_Id(Integer userId, Pageable pageable);

}

