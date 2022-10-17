package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;

@Service
public class BrandService {

	public static final int BRANDS_PER_PAGE = 10;
	@Autowired
	private BrandRepository brandRepo;
	
	public List<Brand> listAll() {
		return (List<Brand>) brandRepo.findAll();
		
	}
	
	public Page<Brand> listByPage(int pageNum,String sortField,String sortDir,String keyword){
		
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum-1, BRANDS_PER_PAGE, sort);
		
		if(keyword != null) {
			return brandRepo.findAll(keyword, pageable);
		}
		return brandRepo.findAll(pageable);
	}
	
	public Brand get(Integer id) throws BrandNotFoundException {
		try {
			return brandRepo.findById(id).get();
		}
		catch(NoSuchElementException ex) {
			throw new BrandNotFoundException("No Brand found with ID " +id);
		}
	}
	
	public void delete(Integer id) throws BrandNotFoundException {
		
		Long count = brandRepo.countById(id);
		if(count == null || count ==0) {
			throw new BrandNotFoundException("Could not find brand with ID : "+id);
		}
		brandRepo.deleteById(id);
		
	}
	
	public Brand save(Brand brand) {
		return brandRepo.save(brand);
	}
	
	public String checkUnique(Integer id,String name) {
		boolean isCreatingNew = (id==null|| id==0);
		Brand brandbyName = brandRepo.findByName(name);
		if(isCreatingNew) {
			if(brandbyName!=null) {
				return "Duplicate";
			}
		}else if(brandbyName != null && brandbyName.getId()!=id) {
			return "Duplicate";
		
		}
		
		return "OK";
	}
}
