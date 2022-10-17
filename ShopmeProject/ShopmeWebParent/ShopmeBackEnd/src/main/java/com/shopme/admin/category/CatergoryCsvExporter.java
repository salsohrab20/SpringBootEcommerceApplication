package com.shopme.admin.category;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;

public class CatergoryCsvExporter {

	public void Export(List<Category> categoryLists, HttpServletResponse response) throws IOException {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timestamp = dateFormatter.format(new Date());
		
		String fileName="users_"+timestamp+".csv";
		response.setContentType("text/csv");
		
		String headerType = "Content-Disposition";
		String headerValue = "attachment;filename="+fileName;
		response.setHeader(headerType, headerValue);
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"Category ID","Category Name"};
		
		String[] fieldMapping = {"id","name"};
		
		csvWriter.writeHeader(csvHeader);
		for(Category category : categoryLists) {
			category.setName(category.getName().replace("--"," "));
			csvWriter.write(category,fieldMapping);
		}
		
		csvWriter.close();
	}
}
