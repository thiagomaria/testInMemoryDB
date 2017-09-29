package com.example.demo;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@Autowired
	private TestService testService;
	
	@RequestMapping(
			value = "/api/test", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Collection<Test> getTests() 
	{
		return testService.findAll();
	}
	
	@RequestMapping(
			value = "/api/test/{id}", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Test getOneTest(@PathVariable("id") Long id) 
	{
		return testService.findOne(id);
	}

	@RequestMapping(
			value = "/api/test", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Test createTest(@RequestBody Test test)
	{
		return testService.create(test);
	}
	
	@RequestMapping(
			value = "/api/test", 
			method = RequestMethod.PUT, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Test updateTest(@RequestBody Test test)
	{
		return testService.update(test);
	}

	@RequestMapping(
			value = "/api/test/{id}", 
			method = RequestMethod.DELETE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value=HttpStatus.OK)
	public void deleteTest(@PathVariable("id") Long id) 
	{
		testService.delete(id);
	}
}
