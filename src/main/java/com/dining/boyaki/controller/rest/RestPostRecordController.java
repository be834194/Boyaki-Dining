package com.dining.boyaki.controller.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	/*@GetMapping(value = "/api/boyaki/all")
    public String getAllPostRecord() throws JsonProcessingException{
		String jsonMsg = null;
		int[]category = null;
		int[]status = null;
		String text = null;
		List<PostRecord>records =  postService.searchPostRecord(category,status,text);
		
		ObjectMapper mapper = new ObjectMapper();
        jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        
        return jsonMsg;
	}*/
	
	@RequestMapping(value = "/api/search",method= {RequestMethod.GET,RequestMethod.POST},
			        produces = "application/json; charset=utf-8")
    @ResponseBody
	public String getSearchPostRecord(@RequestParam(value="category",required=false) int[]category,
    		                          @RequestParam(value="status",required=false) int[]status,
    		                          @RequestParam(value="keyword",required=false) String text)
    		                        		  throws JsonProcessingException{
		String jsonMsg = null;
        List<PostRecord>records =  postService.searchPostRecord(category,status,text);
		
		ObjectMapper mapper = new ObjectMapper();
        jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        
        return jsonMsg;
	}

}
