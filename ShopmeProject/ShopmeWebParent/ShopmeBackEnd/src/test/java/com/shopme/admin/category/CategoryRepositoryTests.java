package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql=false)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
	
	@Autowired
	private CategoryRepository catRepo;
	
	@Test
	public void testCreateRootCategory()
	{
		Category category = new Category("Electronics");
		Category savedCat = catRepo.save(category);
		
		assertThat(savedCat.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateSubCategory()
	{
		Category parent = new Category(4);
		Category cameras = new Category("Gaming Laptops",parent);
		//Category smartPhones = new Category("Smartphones",parent);
		//.saveAll(Arrays.asList(cameras,smartPhones));
		Category savedCat = catRepo.save(cameras);
		
		assertThat(savedCat.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testGetCategory()
	{
		Category category = catRepo.findById(1).get();
		System.out.println(category.getName());
		
		Set<Category> child = category.getChildren();
		for(Category subCategory : child)
		{
			System.out.println(subCategory.getName());
		}
		
		assertThat(child.size()).isGreaterThan(0);
	}
	
	@Test
	public void testPrintHierarchicalCategories() {
		Iterable<Category> categories = catRepo.findAll();
		
		for(Category category : categories)
		{
			if(category.getParent()==null) {
				System.out.println(category);
				
				Set<Category> children = category.getChildren();
				
				for(Category subCategory : children) {
					System.out.println("--"+subCategory.getName());
					printChildren(subCategory,1);
				}
			}
		}
		
	}
	
	private void printChildren(Category parent,int subLevel) {
		int newSubLevel = subLevel+1;
		for(Category subCategory : parent.getChildren())
		{
			for(int i=0;i<newSubLevel;i++)
			{
				System.out.print("--");
			}
			System.out.println(subCategory.getName());
			
			printChildren(subCategory,newSubLevel);
		}
	}
	
	@Test
	public void testListRootCategories() {
		List<Category> listRoot = catRepo.findRootCategories(Sort.by("name").ascending());
		listRoot.forEach(cat-> System.out.println(cat.getName()));
		
	}
	
	@Test
	public void testFindbyName()
	{
		String name="Computers";
		Category category = catRepo.findByName(name);
		
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
		
	}
	
	
	@Test
	public void testFindbyAlias()
	{
		String alias="electronics";
		Category category = catRepo.findByAlias(alias);
		
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(alias);
		
	}
}
