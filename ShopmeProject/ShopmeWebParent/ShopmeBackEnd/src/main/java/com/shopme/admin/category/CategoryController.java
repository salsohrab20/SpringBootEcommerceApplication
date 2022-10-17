package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;
import com.shopme.common.exceptions.CategoryNotFoundException;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService catService;

	boolean editFlag = false;
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword,
			Model modal) {
		
		if (sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		
		CategoryPageInfo pageInfo =  new CategoryPageInfo();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		List<Category> listCategories = catService.listByPage(pageInfo,pageNum, sortDir,keyword);
		
		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE+1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE-1;
		if(endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		
		modal.addAttribute("totalPages", pageInfo.getTotalPage());
		modal.addAttribute("totalItems",pageInfo.getTotalElements());
		modal.addAttribute("currentPage", pageNum);
		modal.addAttribute("sortField", "name");
		modal.addAttribute("sortDir", sortDir);
		modal.addAttribute("keyword", keyword);
		modal.addAttribute("listCategories", listCategories);
		modal.addAttribute("reverseSortDir", reverseSortDir);
		modal.addAttribute("startCount", startCount);
		modal.addAttribute("endCount", endCount);

		return "categories/categories";
		
	}

	@GetMapping("/categories")
	public String listFirstPage(@Param("sortDir") String sortDir, Model modal) {

		return listByPage(1, "asc",null, modal);
	}

	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories = catService.listCategoriesUsedInForm();
		model.addAttribute("category", new Category());
		model.addAttribute("pageTitle", "Create new Category");
		model.addAttribute("listCategories", listCategories);
		return "categories/category_form";
	}

	@PostMapping("/categories/save")
	public String saveCategory(Category category, RedirectAttributes redirectAtrributes,
			@RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);

			Category savedCategory = catService.save(category);
			String uploadDir = "../category-image/" + savedCategory.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			catService.save(category);
		}
		redirectAtrributes.addFlashAttribute("message", "Categories saved Successfully");
		return "redirect:/categories";
	}

	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		editFlag = true;
		try {
			Category category = catService.get(id);
			List<Category> listCategories = catService.listCategoriesUsedInForm();
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("category", category);
			model.addAttribute("pageTitle", "Edit Category (ID :" + id + ")");
			if (editFlag == true) {
				redirectAttributes.addFlashAttribute("message", "User upated successfully");
			} else {
				redirectAttributes.addFlashAttribute("message", "User Saved successfully");
			}
			return "categories/category_form";
		} catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/categories/categories";
		}

	}

	@GetMapping("/categories/delete/{id}")
	public String deleteUsers(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model)
			throws CategoryNotFoundException {
		try {
			catService.delete(id);
			String categoryDir = "../category-image/"+id;
			FileUploadUtil.removeDir(categoryDir);
			
			redirectAttributes.addFlashAttribute("message",
					"The category ID "
					+id+ " has been deleted successfully");
		}catch(CategoryNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/categories";
	}

	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {

		catService.updateEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The user with User id" + id + "has been" + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/categories";

	}

	
	  @GetMapping("/categories/export/csv") 
	  public void exportTOCSV(HttpServletResponse response)
	  throws IOException 
	  {
		  List<Category> categoryLists = catService.listCategoriesUsedInForm();
		  CatergoryCsvExporter exporter = new CatergoryCsvExporter(); exporter.Export(categoryLists,response); 
	  }
	 

}
