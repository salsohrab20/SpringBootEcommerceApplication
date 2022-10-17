package com.shopme.admin.brand;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;

public interface BrandRepository extends PagingAndSortingRepository<Brand,Integer> {

	public Long countById(Integer id);
	
	public Brand findByName(String name);
	
	@Query("Select b from Brand b where b.name like %?1%")
	public Page<Brand> findAll(String keyword,Pageable pageable);
	
	//projection is spring JPA
	@Query("Select NEW Brand(b.id,b.name) from Brand b ORDER BY b.name asc")
	public List<Brand> findAll();
}
