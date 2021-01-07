package team.zerozone.myschool.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import team.zerozone.myschool.entity.LeaveInfo;
import team.zerozone.myschool.entity.Result;
import team.zerozone.myschool.entity.User;
import team.zerozone.myschool.repository.LeaveRepository;
import team.zerozone.myschool.repository.UserRepository;
import team.zerozone.myschool.service.LeaveService;

@Service
public class LeaveServiceImpl implements LeaveService {
	
	@Autowired
	private LeaveRepository leaveRepo;
	@Autowired
	private UserRepository userRepo;

	//查找该学生的请假请求
	@Override
	public Result findLeaveApply(LeaveInfo leaveInfo) {
		
		Result result = new Result();
		List<LeaveInfo> infos = new ArrayList<>();
		LeaveInfo info = new LeaveInfo();
		Integer leaveId;
		
		leaveInfo.setStatus("auditing");
		//查找该学生有无审核中的请求
		leaveId = leaveRepo.findIdForAuditingByUid(leaveInfo);
		if(leaveId != null) {	//学生有审核中的请求
			result.setMsg("auditing");
			result.setDetail(leaveRepo.findLeaveInfoById(leaveId));
		}
		else {	
			infos = leaveRepo.findInfosByUid(leaveInfo.getUid());
			
			if(infos.size() == 0) 	//该学生没有请求
				result.setMsg("empty");
			else {
				info = infos.get(infos.size() - 1);
				
				//该学生最后一项请求是“已批准”
				if(info.getStatus().equals("accepted")) {
					result.setMsg("empty");
				}
				else {//该学生最后一项请求是“已拒绝”
					result.setMsg("denied");
					result.setDetail(leaveRepo.findLeaveInfoById(info.getLeaveId()));
				}
			}
		}
		
		User user = userRepo.findUserByUid(leaveInfo.getUid());
		result.setAccess(user.getUsername());
		
		return result;
	}

	//写入新的请假申请
	@Override
	public Result addNewApply(LeaveInfo leaveInfo) {
		
		Result result = new Result();
		
		leaveRepo.insertNewLeaveInfo(leaveInfo);
		result.setMsg("success");
		
		return result;
	}

	//查询所有处于审核状态的请假申请
	@Override
	public Result findAllAuditingApply() {
		
		Result result = new Result();
		
		List<LeaveInfo> applies = leaveRepo.findAllAuditingInfos();
		result.setDetail(applies);
		
		//根据uid查询用户名
		List<String> usernames = new ArrayList<>();
		for(LeaveInfo apply: applies) {
			String username = userRepo.findUserByUid(apply.getUid()).getUsername();
			usernames.add(username);
		}
		result.setUsernames(usernames);
		
		return result;
	}

	//更新申请
	@Override
	public Result updateLeaveApply(LeaveInfo leaveInfo) {
		
		Result result = new Result();
		String uid = leaveInfo.getUid();
		String status = leaveInfo.getStatus();
		Integer leaveId = leaveInfo.getLeaveId();
		
		leaveRepo.updateLeaveApply(status, leaveId);
		if(status.equals("accepted"))
			userRepo.updateLeaveBalance(uid);
		
		result = findAllAuditingApply();
		
		return result;
	}
}
