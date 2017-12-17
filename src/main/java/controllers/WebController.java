package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

	@RequestMapping("/")
	public String index(Model model) {
		System.out.println("Serving index.html");
		return "index";
	}

	@RequestMapping("/info")
	public String info(Model model) {

		return "info";
	}

	@RequestMapping(value = "/testportal")
	public String servePage(Model m) {
		System.out.println("Serving testportal.html");
		return "testPortal";
	}

}
