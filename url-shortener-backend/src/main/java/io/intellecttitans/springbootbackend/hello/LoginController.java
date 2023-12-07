package io.intellecttitans.springbootbackend.hello;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@CrossOrigin(origins = {"http://localhost:3000","https://rice-comp-539-spring-2022.uk.r.appspot.com"})
public class LoginController {
	
	@RequestMapping(value="/loginUser")
	public String loginUser() {
		return "User Logged In";
	}
	
//	@GetMapping("/")
//    public String redirectToSpecificUrl() {
//        String specificUrl = "http://localhost:3000"; // Replace with your desired URL
//        return "forward:/index.html";
//    }

}
  