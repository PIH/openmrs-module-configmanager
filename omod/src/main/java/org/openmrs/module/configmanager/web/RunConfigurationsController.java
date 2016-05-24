package org.openmrs.module.configmanager.web;

import org.openmrs.module.configmanager.ConfigUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RunConfigurationsController {
	
	@RequestMapping("/module/configmanager/runConfigurations")
	public void runConfigurations() throws Exception {
        ConfigUtil.runConfigurations();
	}
}
