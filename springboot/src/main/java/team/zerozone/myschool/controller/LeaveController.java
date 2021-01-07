package team.zerozone.myschool.controller;

import javax.persistence.PostRemove;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import team.zerozone.myschool.entity.LeaveInfo;
import team.zerozone.myschool.entity.Result;
import team.zerozone.myschool.service.LeaveService;

@RestController
public class LeaveController {
	
	@Autowired
	private LeaveService leaveService;

	
	@PostMapping("/checkApply")
	public Result checkForApplyPage(@RequestBody LeaveInfo leaveInfo) {
		
		System.out.println("check apply called");
		return leaveService.findLeaveApply(leaveInfo);
	}
	
	@PostMapping("/addApply")
	@ResponseBody
	public Result addNewApply(@RequestBody LeaveInfo leaveInfo) {
		
		System.out.println("add new apply called");
		return leaveService.addNewApply(leaveInfo);
	}
	
	@GetMapping("/getApply")
	@ResponseBody
	public Result getAllApply() {
		
		System.out.println("get all apply called");
		return leaveService.findAllAuditingApply();
	}
	
	@PutMapping("/updateApply")
	@ResponseBody
	public Result updateApply(@RequestBody LeaveInfo leaveInfo) {
		
		System.out.println("update apply called");
		return leaveService.updateLeaveApply(leaveInfo);
	}
}
