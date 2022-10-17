package com.shopme.admin.products;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.exceptions.ProductNotFoundException;

@Service
@Transactional
public class ProductService {

	public static final int PRODUCTS_PER_PAGE =5;
	@Autowired
	private ProductRepository productRepo;
	
	public List<Product> listAll(){
		return (List<Product>) productRepo.findAll();
	}
	
	public Product save(Product product) {
		if(product.getId() == null) {
			product.setCreatedTime(new Date());
			
		}
		
		if(product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		} 
		else {
			product.setAlias(product.getName().replaceAll(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		
		return productRepo.save(product);
	}
	
	public Page<Product> listByPage(int pageNum,String sortField,String sortDir,String keyword
			,Integer categoryId){
			
			Sort sort = Sort.by(sortField);
			sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
			
			Pageable pageable = PageRequest.of(pageNum-1, PRODUCTS_PER_PAGE, sort);
			
			if(keyword != null && !keyword.isEmpty()) {
				if(categoryId != null && categoryId>0) {
					String categoryIdMatch = "-" + String.valueOf(categoryId)+"-";
					return productRepo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
				}	 
			}
			
			if(categoryId != null && categoryId>0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId)+"-";
				return productRepo.findAllinCategory(categoryId, categoryIdMatch, pageable);
			}
			return productRepo.findAll(pageable);
		}
	
	public String checkUnique(Integer id , String name) {
		
		boolean isCreatingNew = (id==null || id==0);
		Product product = productRepo.findByName(name);
		if(isCreatingNew) {
			if(product!=null) {
				return "Duplicate";
			}
		}else if(product!=null && product.getId() != id) {
			return "Duplicate";
		}
		return "OK";
		
	}
	
	public void saveProductPrice(Product productinForm) {
		Product productinDB = productRepo.findById(productinForm.getId()).get();
		productinDB.setCost(productinForm.getCost());
		productinDB.setDiscountPercent(productinForm.getDiscountPercent());;
		productinDB.setPrice(productinForm.getPrice());
		productRepo.save(productinDB);
		
	}
	
	public void updateEnabledStatus(Integer id,boolean enabled) {
		productRepo.updateEnabledStatus(id, enabled);
	}
	
	public void delete(Integer id) throws ProductNotFoundException {
		try {
			productRepo.deleteById(id);
		}catch(NoSuchElementException e) {
			throw new ProductNotFoundException("No product with ID " + id + " Exists");
		}
		
	}
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
		return productRepo.findById(id).get();
		}catch(NoSuchElementException e) {
			throw new ProductNotFoundException("Could not find any product with id :"+id);
		}
	}
	
}
