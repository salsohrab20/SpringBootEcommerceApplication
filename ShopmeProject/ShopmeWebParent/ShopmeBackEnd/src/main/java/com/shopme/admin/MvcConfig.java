package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		//user-photos
		this.exposeDirectory("user-photos", registry);
		
		//category-photos
		this.exposeDirectory("../category-image", registry);
		
		//brand-photos
		this.exposeDirectory("../brand-logos", registry);
		
		//products-photos
		this.exposeDirectory("../product-images", registry);
		
		//mainsite-photos
		this.exposeDirectory("../site-logo", registry);
		
	}
	
	private void exposeDirectory(String pathPattern,ResourceHandlerRegistry registry) {
		
		Path path = Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath();
		
		String logicalPath = pathPattern.replace("../", "")+ "/**";
		
		registry.addResourceHandler(logicalPath)
		.addResourceLocations("file:/"+absolutePath+"/");
	}
	
}
