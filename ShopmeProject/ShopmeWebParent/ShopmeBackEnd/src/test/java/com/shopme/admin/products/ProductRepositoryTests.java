package com.shopme.admin.products;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		
		Brand brand = entityManager.find(Brand.class, 2);
		Category category = entityManager.find(Category.class, 6);
		
		Product product = new Product();
		product.setName("Acer");
		product.setAlias("acer");
		product.setShortDescription("A good laptop");
		product.setFullDescription("This is a very good laptop full of new resouces and and ");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(30000);
		product.setCost(10000);
		product.setEnabled(true);
		product.setInStock(true);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product savedProduct = repo.save(product);
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testlistAll()
	{
		Iterable<Product> productLists = repo.findAll();
		productLists.forEach(System.out::println);
		
		assertThat(productLists).hasSizeGreaterThan(0);
	}
	
	@Test
	public void testdelete() {
		Integer id =1;
		repo.deleteById(id);
	
		Optional<Product> result = repo.findById(id);
		
		assertThat(!result.isPresent());
	}
	
	@Test
	public void testGetProduct() {
		Integer id =1;
		Product product = repo.findById(id).get();
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testUpdate()
	{
		Integer id =1;
		Product product = repo.findById(id).get();
		product.setPrice(499);
		repo.save(product);
		Product updatedProduct = entityManager.find(Product.class, id);
		
		assertThat(updatedProduct.getPrice()).isEqualTo(499);
	}
	
	@Test
	public void testSaveProductwithImages() {
		
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.setMainImage("main image.jpg");
		product.addExtraImage("extra image 1");
		product.addExtraImage("extra_image 2.jpg");
		product.addExtraImage("extra-image 3.jpg");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
		
		
	}
	
	@Test
	public void testSaveProductwithDetails() {
		
		Integer productId = 6;
		Product product = repo.findById(productId).get();
		
		product.addDetail("Device Memory", "128 GB");
		product.addDetail("CPU model", "mediaTek");
		product.addDetail("OS", "Android 10");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getDetails().size()).isEqualTo(12);
		
		
	}
	
}
