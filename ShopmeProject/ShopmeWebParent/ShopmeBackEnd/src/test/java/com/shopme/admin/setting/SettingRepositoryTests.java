package com.shopme.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTests {

	@Autowired private SettingRepository repo;
	
	@Test
	public void testCreateGeneralSettings() {
		//Setting siteName = new Setting("SITE_NAME","Shopme",SettingCategory.GENERAL);
		Setting siteLogo = new Setting("SITE_LOGO","Shopme.png",SettingCategory.GENERAL);
		Setting sitecopyright = new Setting("COPYRIGHT","Copyright (C) 2021 Shopme LTD",SettingCategory.GENERAL);
		//Setting savedSetting = repo.save(siteName);
		repo.save(siteLogo);
		repo.save(sitecopyright);
		//assertThat(savedSetting).isNotNull();
	}
	
	@Test
	public void testCreateCurrencySettings() {
		Setting currencyId=new Setting("CURRENCY_ID","1",SettingCategory.CURRENCY);
		Setting symbol=new Setting("CURRENCY_SYMBOL","$",SettingCategory.CURRENCY);
		Setting symbolPosition=new Setting("CURRENCY_SYMBOL_POSITION","before",SettingCategory.CURRENCY);
		Setting decimalPointType=new Setting("DECIMAL_POINT_TYPE","POINT",SettingCategory.CURRENCY);
		Setting decimalDigits=new Setting("DECIMAL_DIGITS","2",SettingCategory.CURRENCY);
		Setting thousandPointType=new Setting("THOUSANDS_POINT_TYPE","COMMA",SettingCategory.CURRENCY);
		
		List<Setting> settingList = new ArrayList();
		settingList.add(currencyId);
		settingList.add(symbol);
		settingList.add(symbolPosition);
		settingList.add(decimalPointType);
		settingList.add(decimalDigits);
		settingList.add(thousandPointType);
		
		
		repo.saveAll(settingList);
		
		
	}
	
	@Test
	public void testListSettingsByCategory() {
		List<Setting> settings = repo.findByCategory(SettingCategory.GENERAL);
		settings.forEach(System.out::println);
	}
}
