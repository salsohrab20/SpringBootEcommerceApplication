package com.shopme.admin.user;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewUserWithOneRole()
	{
		Role roleAdmin=entityManager.find(Role.class,1);
		User userName=new User("salman@javastar.com","salman20","Salman","Sohrab");
		userName.addRole(roleAdmin);
		
		User savedUser=userRepo.save(userName);
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateNewUserWithTwoRoles()
	{
		User userNames=new User("sohrab@javahero.com","salman20","Sohrab","Ansari");
		Role roleEditor=new Role(3);
		Role roleAssistant=new Role(5);
		userNames.addRole(roleEditor);
		userNames.addRole(roleAssistant);
		
		User savedUser=userRepo.save(userNames);
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllUsers()
	{
		Iterable<User> listUsers=userRepo.findAll();
		listUsers.forEach(user->System.out.println(user));
		
		
	}
	
	@Test
	public void testGetUserbyID()
	{
		User userNam=userRepo.findById(1).get();  //getting as findbyId returns an Optional class
		System.out.println(userNam);
		assertThat(userNam).isNotNull();
		
	}
	
	@Test
	public void testUpdateUserDetails()
	{
		User userNam=userRepo.findById(1).get();  //getting as findbyId returns an Optional class
		userNam.setEnabled(true);
		userNam.setEmail("spring@star.com");
		userRepo.save(userNam);
		
	}
	
	@Test
	public void testUpdateUserRoles()
	{
		User userNam=userRepo.findById(2).get(); 
		Role roleEditor=new Role(3);
		Role roleSalesperson=new Role(2);
		
		userNam.getRoles().remove(roleEditor);

		userNam.addRole(roleSalesperson);
		userRepo.save(userNam);
		
	}
	
	@Test
	public void testDeleteUser()
	{
		Integer userId=2; //getting as findbyId returns an Optional class
		
		userRepo.deleteById(userId);
		
	}
	
	@Test
	public void testUserEmail()
	{
		String email="sohrab@javahero.com";
		User userEmail=userRepo.getUserbyEmail(email);
		assertThat(userEmail).isNotNull();
		
	}
	
	@Test
	public void testCountByID()
	{
		Integer id=14;
		Long countbyID=userRepo.countById(id);
		assertThat(countbyID).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisableUser()
	{
		Integer id=3;
		userRepo.updateEnabledStatus(id,false);
	}
	
	@Test
	public void testEnableUser()
	{
		Integer id=3;
		userRepo.updateEnabledStatus(id,true);
	}
	
	@Test
	public void testListFirstPage()
	{
		int pageNumber=0;
		int pageSize=4;
		Pageable pageable=PageRequest.of(pageNumber, pageSize);
		Page<User> page=userRepo.findAll(pageable);
		List<User> listUsers=page.getContent();
		listUsers.forEach(user->System.out.println(user));
		assertThat(listUsers.size()).isEqualTo(pageSize);
		
	}
	
	@Test
	public void testSearchUsers()
	{
		Pageable pageable = PageRequest.of(0,4);
		Page<User> page = userRepo.findAll("bruce",pageable);
		
		List<User> listUsers = page.getContent();
		listUsers.forEach(user-> System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
	}
}
