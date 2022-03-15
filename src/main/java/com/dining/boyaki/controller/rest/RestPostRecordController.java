package com.dining.boyaki.controller.rest;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.service.PostService;

@RestController
public class RestPostRecordController {
	
	private final PostService postService;
	
	public RestPostRecordController(PostService postService) {
		this.postService = postService;
	}
	
	@RequestMapping(value = "/api/search",method= {RequestMethod.GET,RequestMethod.POST},
			        produces = "application/json; charset=utf-8")
    @ResponseBody
	public String getSearchPostRecord(@RequestParam(value="category") int[]category,
    		                          @RequestParam(value="status") int[]status,
    		                          @RequestParam(value="keyword") String text,
    		                          @RequestParam(value="page") int page)
    		                        		  throws JsonProcessingException{
		String jsonMsg = null;
		System.out.println(page);
        List<PostRecord>records =  postService.searchPostRecord(category,status,text,page);
		
		ObjectMapper mapper = new ObjectMapper();
        jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        
        return jsonMsg;
	}

}
