package team.zerozone.myschool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import team.zerozone.myschool.entity.Result;
import team.zerozone.myschool.entity.User;
import team.zerozone.myschool.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/test")
	public void test() {
		System.out.println("test");
	}
	
	//登录控制
	@PostMapping("/login")
	@ResponseBody
	public Result login(@RequestBody User user) {
		
		System.out.println("login called");
		
		return userService.checkForLogin(user);
	}
	
	@PostMapping("/getUser")
	@ResponseBody
	public User getUserInfo(@RequestBody String uid) {
		
		System.out.println("get user called");
		
		return userService.findUserByUid(uid);
	}
	
	@PutMapping("/update")
	public Result updateUserInfo(@RequestBody User user) {
		
		//To DO
		System.out.println("update called");
		return userService.updateUserInfo(user);
	}
	
	@PostMapping("/add")
	@ResponseBody
	public Result insertNewStudent(@RequestBody User user) {
		
		System.out.println("add called");
		return userService.insertUser(user);
	}
	
	@GetMapping("/batches")
	@ResponseBody
	public Result getAllBatches() {
		
		System.out.println("get batches called");
		return userService.findBatch();
	}
	
	@PostMapping("/batch/student")
	@ResponseBody
	public Result getUsersByBatch(@RequestBody User user) {
		
		System.out.println("get simple batch called");
		return userService.findUsersByBatch(user);
	}
	
	
	//测试用
	@GetMapping("/login/{username}")
	@ResponseBody
	public User sendUserInfo(@PathVariable(name = "username")String username) {
		
		User user = userService.findUserByUsername(username);
		
		if(user != null)
			return user;
		else {
			user = new User();
			user.setUid("0");
			return user;
		}
	}

}
