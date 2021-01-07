package team.zerozone.myschool.service;

import team.zerozone.myschool.entity.Result;
import team.zerozone.myschool.entity.User;

public interface UserService {
	
	Result checkForLogin(User user);
	
	User findUserByUid(String uid);
	
	User findUserByUsername(String username);
	
	Result updateUserInfo(User user);
	
	Result insertUser(User user);
	
	Result findBatch();
	
	Result findUsersByBatch(User user);

}
