package com.example.demo;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

	@Autowired
	private TestRepository testRepository;
	
	public Collection<Test> findAll()
	{
		Collection<Test> allEntries = testRepository.findAll();
		return allEntries;
	}
	
    public Test findOne(Long id)
    {
    	Test entry = testRepository.findOne(id);
    	return entry;
    }
    
    public Test create(Test test)
    {
    	if(test.getId() != null)
    	{
    		//id is auto generated
    		return null;
    	}
    	Test generatedTest = testRepository.save(test);
    	return generatedTest;
    }
    
    public Test update(Test test)
    {
    	Test persisted = testRepository.findOne(test.getId());
    	if(persisted == null)
    	{
    		return null;
    	}
    	Test updatedTest = testRepository.save(test);
    	return updatedTest;
    }
    public void delete(Long id)
    {
    	testRepository.delete(id);

    }
    
}
