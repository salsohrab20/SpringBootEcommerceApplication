package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.Setting;

@Controller
public class SettingController {

		@Autowired private SettingService service;
		
		@Autowired private CurrencyRepository currencyRepo;
		
		@GetMapping("/settings")
		public String listAll(Model model) {
			
			List<Setting> listSettings= service.listAllSettings();
			List<Currency> listCurrencies = currencyRepo.findAllByOrderByNameAsc();
			
			for(Setting setting : listSettings) {
				model.addAttribute(setting.getKey(), setting.getValue());
			}
			model.addAttribute("listCurrencies",listCurrencies);
			
			
			return "settings/settings";
		}
		
		@PostMapping("/settings/save_general")
		public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
				HttpServletRequest request,RedirectAttributes rs) throws IOException {
			
			GeneralSettingBag settingBag = service.getGeneralSettings();
			saveSiteLogo(multipartFile, settingBag);
			saveCurrencySymbol(request,settingBag);
			updateSettingValuesFromForm(request,settingBag.list());
			
			rs.addFlashAttribute("message", "General settings has been saved");
			return "redirect:/settings";
		}

		private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
			if(!multipartFile.isEmpty()) {
				String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				String value = "/site-logo/"+filename;
				settingBag.updateSiteLogo(value);
				
				String uploadDir="../site-logo/";
				FileUploadUtil.cleanDir(uploadDir);
				FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
			}
		}
		
		private void saveCurrencySymbol(HttpServletRequest request,GeneralSettingBag generalSettingBag) {
			
			Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));		
			Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);	
			
			if(findByIdResult.isPresent()) {
				Currency currency = findByIdResult.get();
				String symbol= currency.getSymbol();
				generalSettingBag.updateCurrencySymbol(currency.getSymbol());
			}
		}
		
		private void updateSettingValuesFromForm(HttpServletRequest request,List<Setting> listSettings) {
			
			for(Setting setting : listSettings) {
				String value = request.getParameter(setting.getKey());
				if(value != null) {
					setting.setValue(value);
				}
			}
			
			service.saveAll(listSettings);
		}
		
		@PostMapping("/settings/save_mail_server")
		public String saveEmailServerSettings(HttpServletRequest request,RedirectAttributes rs) {
			List<Setting> mailServerSettings = service.getMailServerSettings();
			updateSettingValuesFromForm(request, mailServerSettings);
			
			rs.addFlashAttribute("message","Mail Server settings have been saved");
			
			return "redirect:/settings";
			
		}
		
		@PostMapping("/settings/save_mail_templates")
		public String saveEmailTSettings(HttpServletRequest request,RedirectAttributes rs) {
			List<Setting> mailTemplateSettings = service.getMailTemplateSettings();
			updateSettingValuesFromForm(request, mailTemplateSettings);
			
			rs.addFlashAttribute("message","Mail Template settings have been saved");
			
			return "redirect:/settings";
			
		}
}
