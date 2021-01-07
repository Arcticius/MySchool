package team.zerozone.myschool.service;

import team.zerozone.myschool.entity.LeaveInfo;
import team.zerozone.myschool.entity.Result;

public interface LeaveService {
	
	Result findLeaveApply(LeaveInfo leaveInfo);
	
	Result addNewApply(LeaveInfo leaveInfo);
	
	Result findAllAuditingApply();
	
	Result updateLeaveApply(LeaveInfo leaveInfo);

}
