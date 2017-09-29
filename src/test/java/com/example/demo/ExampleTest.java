package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringContextTestConfiguration.class})
@WebAppConfiguration
public class ExampleTest extends AbstractIntegrationTest {

	@Autowired
    private TestController testController;

	private MockMvc mvc;

	@Test
	public void createTest() throws Exception {
		mvc = MockMvcBuilders
				.standaloneSetup(testController)
				.build();
		
		mvc.perform(post("/api/test").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{\"name\":\"test\"}"))
				.andExpect(status().isOk());
		
		verifyDatasetForTable("test-dataset.xml", "TEST", new String[] {});

	}
	
	@Test
	public void readAllTests() throws Exception {
		mvc = MockMvcBuilders
				.standaloneSetup(testController)
				.build();
		runSQLCommands("/insert-tests.sql");
		final String expectedResult = "[{\"id\":1,\"name\":\"Thiago\"},{\"id\":2,\"name\":\"Caio\"}]";
		
		MvcResult result = mvc.perform(get("/api/test")).andExpect(status().isOk()).andReturn();
		assertEquals(expectedResult, result.getResponse().getContentAsString());
		
	}
}
