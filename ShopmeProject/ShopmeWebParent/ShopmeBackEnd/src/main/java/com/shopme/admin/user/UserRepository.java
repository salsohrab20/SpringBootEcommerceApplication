package com.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User,Integer> {
	
	@Query("Select u from User u where u.email=:email")
	public User getUserbyEmail(@Param("email") String email);

	public Long countById(Integer id);
	
	@Query("Select u from User u where CONCAT(u.id,' ', u.firstName,' ' ," + "u.lastName) like %?1%")
	public Page<User> findAll(String keyword,Pageable pageable);
	
	@Query("Update User u set u.enabled=?2 where u.id=?1")
	@Modifying                       //as it is modifying the db
	public void updateEnabledStatus(Integer id ,boolean enabled);
}
