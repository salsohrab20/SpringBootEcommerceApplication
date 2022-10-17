package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {

	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService catService;
	
	@GetMapping("/brands")
	public String listALL(@Param("sortDir") String sortDir,Model model) {
		return listPage(1,model,"name","asc", null);
	}
	
	
	@GetMapping("/brands/edit/{id}")
	public String EditUser(@PathVariable("id") Integer id,
			RedirectAttributes redirectAttributes,
			Model model) {
		try {
		Brand brand = brandService.get(id);
		List<Category> listCategories = catService.listCategoriesUsedInForm();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle","Edit Category with ID : "+id);
		model.addAttribute("brand", brand);
		
		return "brands/brand_form";
		}
		catch(BrandNotFoundException b) {
			redirectAttributes.addFlashAttribute("message",b.getMessage());
			return "redirect:/brands";
			
		}
	}
	
	

	@GetMapping("/brands/new")
	public String CreateBrand(Model model) {
	
	
		List<Category> listCategories = catService.listCategoriesUsedInForm();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle","Create new Brand");
		model.addAttribute("brand", new Brand());
		
		return "brands/brand_form";
		
	}
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand,@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {
	
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			
			Brand savedBrand = brandService.save(brand);
			String uploadDir = "../brand-logos/"+savedBrand.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		}
		else {
			brandService.save(brand);
		}
		redirectAttributes.addFlashAttribute("message","The brand has been saved Successfully");
		return "redirect:/brands";
		
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable("id") Integer id,
			Model model,
			RedirectAttributes redirectAttributes) throws IOException {
	
		try {
			brandService.delete(id);
			String brandDir = "../brand-logos/"+id;
			FileUploadUtil.removeDir(brandDir);
			
			redirectAttributes.addFlashAttribute("message", "The brand ID "+id+" has been deleted successfully");
			
		}catch(BrandNotFoundException ed){
			redirectAttributes.addFlashAttribute("message", ed.getMessage());
			
		}
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listPage(@PathVariable("pageNum") int pageNum,Model model,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		
		Page<Brand> page = brandService.listByPage(pageNum, sortField, sortDir, keyword);
		
		List<Brand> listBrands = page.getContent();
		
		long startCount = (pageNum-1)*BrandService.BRANDS_PER_PAGE+1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE-1;
		
		if(endCount > page.getTotalElements()) {
			endCount=page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc")?"desc":"asc";
		
		model.addAttribute("currentPage",pageNum);
		model.addAttribute("totalPages",page.getTotalPages());
		model.addAttribute("startCount",startCount);
		model.addAttribute("endCount",endCount);
		model.addAttribute("totalItems",page.getTotalElements());
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("sortField",sortField);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword",keyword);
		model.addAttribute("listBrands",listBrands);
		
		return "brands/brands";
	}
}
