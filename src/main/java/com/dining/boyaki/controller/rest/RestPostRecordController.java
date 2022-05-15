package com.dining.boyaki.controller.rest;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.dining.boyaki.model.entity.CommentRecord;
import com.dining.boyaki.model.entity.PostRecord;
import com.dining.boyaki.model.service.PostService;

@RestController
public class RestPostRecordController {
	
	private final PostService postService;
	
	public RestPostRecordController(PostService postService) {
		this.postService = postService;
	}
	
	@ResponseBody
	@RequestMapping(value = "/api/find",method=RequestMethod.GET,
	                produces = "application/json; charset=utf-8")
	public String findPostRecord(@RequestParam(value="nickName") String nickName,
		                         @RequestParam(value="page") int page)
		                        		  throws JsonProcessingException{
		String jsonMsg = null;
		List<PostRecord>records =  postService.findPostRecord(nickName,page);
		
		ObjectMapper mapper = new ObjectMapper();
		jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
		
		return jsonMsg;
	}
	
	@ResponseBody
	@RequestMapping(value = "/api/search",method=RequestMethod.POST,
			        produces = "application/json; charset=utf-8")
	public String searchPostRecord(@RequestParam(value="category") int[]category,
    		                       @RequestParam(value="status") int[]status,
    		                       @RequestParam(value="keyword") String text,
    		                       @RequestParam(value="page") int page)
    		                        		  throws JsonProcessingException{
		String jsonMsg = null;
        List<PostRecord>records =  postService.searchPostRecord(category,status,text,page);
		
		ObjectMapper mapper = new ObjectMapper();
        jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        
        return jsonMsg;
	}
	
	@ResponseBody
	@RequestMapping(value = "/api/comments",method=RequestMethod.POST,
			        produces = "application/json; charset=utf-8")
	public String searchCommentRecord(@RequestParam(value="postId") long postId,
			                          @RequestParam(value="page") int page)
    		                        		  throws JsonProcessingException{
		String jsonMsg = null;
        List<CommentRecord>records =  postService.findCommentList(postId, page);
		
		ObjectMapper mapper = new ObjectMapper();
        jsonMsg =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        
        return jsonMsg;
	}

}
