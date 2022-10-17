package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {

	@Autowired
	private BrandRepository brandRepo;
	
	
	@Test
	public void testCreateBrand1()
	{
		Brand brand = new Brand("Acer");
		Category laptops = new Category(6);
		brand.getCategories().add(laptops);
		
		Brand saveBrand = brandRepo.save(brand);
		
		assertThat(saveBrand).isNotNull();
		assertThat(saveBrand.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateBrand2()
	{
		Brand brand = new Brand("Apple");
		Category cellphones = new Category(4);
		Category tablets = new Category(7);
		
		brand.getCategories().add(cellphones);
		brand.getCategories().add(tablets);
		
		Brand saveBrand = brandRepo.save(brand);
		
		assertThat(saveBrand).isNotNull();
		assertThat(saveBrand.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateBrand3()
	{
		Brand brand = new Brand("Samsung");
		Category memory = new Category(29);
		Category internatHD = new Category(24);
		
		brand.getCategories().add(memory);
		brand.getCategories().add(internatHD);
		
		Brand saveBrand = brandRepo.save(brand);
		
		assertThat(saveBrand).isNotNull();
		assertThat(saveBrand.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void FindALL() {
		Iterable<Brand> getAll = brandRepo.findAll();
		getAll.forEach(System.out::println);
		assertThat(getAll).isNotEmpty();
	}
	
	@Test
	public void getByID() {
		Brand brand = brandRepo.findById(1).get();
		assertThat(brand.getName()).isEqualTo("Acer");
		
		
	}
}
