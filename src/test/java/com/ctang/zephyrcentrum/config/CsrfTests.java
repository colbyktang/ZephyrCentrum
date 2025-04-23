package com.ctang.zephyrcentrum.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CsrfTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void loginWhenValidCsrfTokenThenSuccess() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"user\",\"password\":\"password\"}"))
			.andExpect(status().isOk());
	}

	@Test
	public void loginWhenInvalidCsrfTokenThenForbidden() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login").with(csrf().useInvalidToken())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"user\",\"password\":\"password\"}"))
			.andExpect(status().isForbidden());
	}

	@Test
	public void loginWhenNoCsrfTokenThenForbidden() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"user\",\"password\":\"password\"}"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser
	public void logoutWhenValidCsrfTokenThenSuccess() throws Exception {
		mockMvc.perform(post("/api/v1/auth/logout").with(csrf())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	public void imageUploadWhenValidCsrfTokenThenSuccess() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
			"files", 
			"test-image.jpg", 
			"image/jpeg", 
			"test image content".getBytes()
		);
		
		mockMvc.perform(multipart("/api/v1/images/upload")
				.file(file)
				.param("userId", "1")
				.with(csrf()))
			.andExpect(status().isCreated());
	}

	@Test
	@WithMockUser
	public void imageUploadWhenNoCsrfTokenThenForbidden() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
			"files", 
			"test-image.jpg", 
			"image/jpeg", 
			"test image content".getBytes()
		);
		
		mockMvc.perform(multipart("/api/v1/images/upload")
				.file(file)
				.param("userId", "1"))
			.andExpect(status().isForbidden());
	}

	@Test
	public void loginWhenValidCsrfTokenButInvalidCredentialsThenUnauthorized() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"user\",\"password\":\"password2\"}"))
			.andExpect(status().isUnauthorized());
	}
}