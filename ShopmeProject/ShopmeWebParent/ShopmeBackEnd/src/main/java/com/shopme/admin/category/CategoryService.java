package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.common.entity.Category;
import com.shopme.common.exceptions.CategoryNotFoundException;

@Service
@Transactional
public class CategoryService {

	public static final int ROOT_CATEGORIES_PER_PAGE=4;
	
	@Autowired
	private CategoryRepository catRepo;
	
	
	
	public List<Category> listByPage(CategoryPageInfo pageInfo,int pageNum,String sortDir,
			String keyword){
		Sort sort = Sort.by("name");
		if(sortDir.equals("asc")) {
			sort=sort.ascending();
		}
		else if(sortDir.equals("desc")) {
			sort=sort.descending();
		}
		
		Pageable pageable = PageRequest.of(pageNum-1, ROOT_CATEGORIES_PER_PAGE,sort);
		
		Page<Category> pageCategories = null;
		if(keyword!=null && !keyword.isEmpty()) {
			pageCategories = catRepo.search(keyword, pageable);
		}
		else
		{
			pageCategories = catRepo.findRootCategories(pageable);
		}
		
		
	
		List<Category> rootCategories = pageCategories.getContent();
		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPage(pageCategories.getTotalPages());
		
		if(keyword!=null && !keyword.isEmpty()) {
			List<Category> searchResult = pageCategories.getContent();
			for(Category category : searchResult) {
				category.setHasChildren(category.getChildren().size()>0);
			}
			return searchResult;
		}else {
			return listHierarchicalCategories(rootCategories,sortDir);
		}
		
	}
	
	public List<Category> listHierarchicalCategories(List<Category> rootCategories,String sortDir){
		
		List<Category> hierarchicalCategories = new ArrayList();
		for(Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.CopyFull(rootCategory));
			
			Set<Category> children = sortSubCategories(rootCategory.getChildren(),sortDir);
			for(Category subCategory : children) {
				String name = "--"+subCategory.getName();
				hierarchicalCategories.add(Category.CopyFull(subCategory, name));
				
				listSubHierarchichalCategories(subCategory,1,hierarchicalCategories,sortDir);
			}
		}
		return hierarchicalCategories;
		
	}
	
	private void listSubHierarchichalCategories(Category parent,int sublevel,List<Category> hierarchicalCategories
			,String sortDir)
	{
		int newsubLvl = sublevel +1;
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		for(Category cat : children)
		{
			String name="";
			for(int i=0;i<newsubLvl;i++)
			{
				name+="--";
			}
			name+=cat.getName();
			hierarchicalCategories.add(Category.CopyFull(cat,name));
			listSubHierarchichalCategories(cat,newsubLvl,hierarchicalCategories,sortDir);
		}
	}
	
	public List<Category> listCategoriesUsedInForm(){
		List<Category> catusedinForm = new ArrayList<>();
		
		Iterable<Category> catInDB = catRepo.findRootCategories(Sort.by("name").ascending());
		for(Category category : catInDB)
		{
			if(category.getParent()==null) {
				catusedinForm.add(Category.copyIdAndNameV1(category));
				
				Set<Category> children = sortSubCategories(category.getChildren());
				
				for(Category subCategory : children) {
					String name = "--"+subCategory.getName();
					catusedinForm.add(Category.copyIdAndNameV2(subCategory.getId(),name));
					listSubCategoriesUsedinForm(subCategory,1,catusedinForm);
				}
			}
		}
		
		return catusedinForm;
	}
	
	private void listSubCategoriesUsedinForm(Category parent,int subLevel,List<Category> catusedinForm) {
		int newSubLevel = subLevel+1;
		Set<Category> children =sortSubCategories(parent.getChildren());
		for(Category subCategory : children)
		{
			String name="";
			for(int i=1;i<=newSubLevel;i++)
			{
				name +="--";
			}
			name+=subCategory.getName();
			catusedinForm.add(Category.copyIdAndNameV2(subCategory.getId(),name));
			listSubCategoriesUsedinForm(subCategory,newSubLevel,catusedinForm);
		}
	}
	
	public Category save(Category category)
	{
		
		 Category parent = category.getParent(); 
		 if(parent != null) 
		 { 
			String allParentIds = parent.getAllParentIDs()==null?"-":parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIds); 
		 }
		 
		return catRepo.save(category);
	}
	
	public Category get(Integer id) throws CategoryNotFoundException {
		try {
		return catRepo.findById(id).get();
		}
		catch(NoSuchElementException e){
			throw new CategoryNotFoundException("Could not find category with ID "+id);
		}
	}
	
	public String checkUnique(Integer id,String name,String alias) {
		boolean isCreatingNew = (id==null || id==0);
		Category categoryByName = catRepo.findByName(name);
		
		if(isCreatingNew) {
			if(categoryByName != null) {
				return "DuplicateName";
			}else {
				if(catRepo.findByAlias(alias)!=null) {
					return "DuplicateAlias";
				};
			}
		} else {
			if(categoryByName != null && categoryByName.getId()!=id) {
				return "DuplicateName";
			}
			Category catByAlias = catRepo.findByAlias(alias);
			 if(catByAlias!=null  && catByAlias.getId()!=id ) {
				return "DuplicateAlias";
		}
		
	}
		return "OK";
	}
	
	public void delete(Integer id) throws CategoryNotFoundException
	{
		long count = catRepo.countById(id);
		if(count ==0) {
			String message = "Category with ID"+id+"is not found" ;
			throw new CategoryNotFoundException(message);
		}
		catRepo.deleteById(id);
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children){
		return sortSubCategories(children,"asc");
	}
	
	
	private SortedSet<Category> sortSubCategories(Set<Category> children,String sortDir){
		
		SortedSet<Category> sortedChildren = new TreeSet(new Comparator<Category>() {

			@Override
			public int compare(Category cat1, Category cat2) {
				if(sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				}else {
					return cat2.getName().compareTo(cat1.getName());
				}
			}
			
		});		
		sortedChildren.addAll(children);
		return sortedChildren;
	}
	
	
	
	public void updateEnabledStatus(Integer id,boolean enabled) {
		
		catRepo.UpdateEnabledStatuc(id, enabled);
	}
}