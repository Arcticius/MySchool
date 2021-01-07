package team.zerozone.myschool.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import team.zerozone.myschool.entity.Result;
import team.zerozone.myschool.entity.User;
import team.zerozone.myschool.repository.UserRepository;
import team.zerozone.myschool.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepo;


	@Override
	public User findUserByUsername(String username) {
		
		return userRepo.findUserByUsername(username);
	}

	//登录检查
	@Override
	public Result checkForLogin(User user) {
		
		Result result = new Result();
		
		result.setMatched(false);
		result.setDetail(null);
		
		//根据request中的username和password获取uid
		String uid = userRepo.findUidByUser(user);
		
		if(uid != null) {//uid不为null，说明用户名与密码匹配，登陆成功
			result.setMsg("success");
			result.setMatched(true);
			User newUser = userRepo.findUserByUid(uid);
			
			if(newUser.getAccess().equals("1")) { //管理员
				List<User> users = userRepo.findUsersByAccess("0");
				result.setDetail(users);
				result.setAccess("1");
			}
			else { 	//学生
				result.setDetail(newUser);
				result.setAccess("0");
			}
		}
		else//uid为null，说明用户名与密码不匹配，登陆失败
			result.setMsg("failed");
		
		return result;
	}

	@Override
	public User findUserByUid(String uid) {
		
		return userRepo.findUserByUid(uid);
	}

	//更新用户信息
	@Override
	public Result updateUserInfo(User user) {
		
		Result result = new Result();
		
		result.setMatched(false);
		result.setDetail(null);
		
		userRepo.updateUserInfo(user);
		result.setMsg("success");
		
		User newUser = userRepo.findUserByUid(user.getUid());
		result.setDetail(newUser);
		
		return result;
	}

	//插入新用户
	@Override
	public Result insertUser(User user) {
		
		Result result = new Result();
		result.setMatched(false);
		
		userRepo.insertUser(user);
		result.setMsg("success");
		
		List<User> users = userRepo.findUsersByAccess("0");
		result.setDetail(users);
		
		return result;
	}

	//查询所有班级名
	@Override
	public Result findBatch() {
		
		Result result = new Result();
		
		List<String> batches = new ArrayList<>();
		batches = userRepo.findBatch();
		
		result.setDetail(batches);
		
		return result;
	}

	//根据班级名查找用户
	@Override
	public Result findUsersByBatch(User user) {
		
		Result result = new Result();
		List<User> users = new ArrayList<>();
		
		String batch = user.getBatch();
		
		//班级为“all”，则等价为查找所有学生（即权限为“0”的用户）
		if(batch.equals("all")) {
			users = userRepo.findUsersByAccess("0");
		}
		else {
			users = userRepo.findUsersByBatch(user.getBatch());			
		}
		result.setDetail(users);
		
		return result;
	}

}
