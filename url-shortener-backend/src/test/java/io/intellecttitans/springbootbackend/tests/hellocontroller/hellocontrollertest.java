package io.intellecttitans.springbootbackend.tests.hellocontroller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.intellecttitans.springbootbackend.hello.HelloController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = HelloController.class)
public class hellocontrollertest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
//	@MockBean
//	private RegisterUseCase registerUseCase;
	@Test
	void whenValidInput_thenReturns200() throws Exception {
		mockMvc.perform(get("/hello"))
			    .andExpect(status().isOk());
	 }

	
}
