package io.intellecttitans.springbootbackend.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class LoginController {
	
	@RequestMapping("/loginUser")
	public String loginUser() {
		return "User Logged In";
	}

}
  