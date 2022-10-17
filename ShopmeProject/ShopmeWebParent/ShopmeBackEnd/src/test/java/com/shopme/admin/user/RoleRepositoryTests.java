package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;

@DataJpaTest  //to leverage the use of datajpa for test
@AutoConfigureTestDatabase(replace=Replace.NONE) //for testing against real db
@Rollback(false)  //commits the change in db 
public class RoleRepositoryTests {

	@Autowired
	private RoleRepository repo;
	
	@Test
	public void testCreateFirstRole()
	{
		Role roleAdmin=new Role("Admin","manage everything");
		Role savedRole=repo.save(roleAdmin);
		assertThat(savedRole.getId()).isGreaterThan(0);  //data persisted in Db
		
	}
	
	@Test
	public void testCreateRestRole()
	{
		Role roleSalesperson=new Role("Salesperson","manage product price,"+"customers,shipping,orders and sales report");
		Role roleEditor=new Role("Editor","manage categories,brands"+"product,articles and menus");
		Role roleAssistant=new Role("Assitant","manage questions and reviews");
		Role roleShipper=new Role("Shipper","view products,view orders and update order status");
		repo.saveAll(Arrays.asList(roleSalesperson,roleEditor,roleAssistant,roleShipper));
		
		
	}
}
