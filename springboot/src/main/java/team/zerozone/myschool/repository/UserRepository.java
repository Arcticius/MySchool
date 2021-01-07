package team.zerozone.myschool.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import team.zerozone.myschool.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

	@Query("select u.uid from User u where username = :#{#user.username} and password = :#{#user.password}")
	String findUidByUser(User user); //根据用户名和密码查询uid
	
	@Query("select u from User u where uid = ?1")
	User findUserByUid(String uid); //根据uid查找用户
	
	@Query("select u from User u where username = ?1")
	User findUserByUsername(String username); //根据用户名查找用户
	
	@Transactional
	@Modifying
	@Query("update User u "
			+ "set u.username = :#{#user.username}, u.dob = :#{#user.dob}, u.bloodGroup = :#{#user.bloodGroup}, u.email = :#{#user.email}, u.contactNumber = :#{#user.contactNumber}, u.address = :#{#user.address} "
			+ "where u.uid = :#{#user.uid}")
	void updateUserInfo(User user); //更新用户信息（除请假剩余次数外）
	
	@Transactional
	@Modifying
	@Query(value = "insert into users(uid, username, password, access, batch, age, dob, blood_group, address, contact_number, email, leave_balance) "
			+ "values(:#{#user.uid}, :#{#user.username}, :#{#user.password}, :#{#user.access}, :#{#user.batch}, :#{#user.age}, :#{#user.dob}, :#{#user.bloodGroup}, :#{#user.address}, :#{#user.contactNumber}, :#{#user.email}, :#{#user.leaveBalance})", 
			nativeQuery = true)
	void insertUser(User user); //插入新用户数据
	
	@Query(value = "select distinct batch from users where batch != 'NIIT'", 
			nativeQuery = true)
	List<String> findBatch(); //查询所有班级名
	
	@Query("select u from User u where batch = ?1")
	List<User> findUsersByBatch(String batch); //根据班级名查找用户
	
	@Query("select u from User u where access = ?1")
	List<User> findUsersByAccess(String access); //根据用户权限查找用户
	
	@Transactional
	@Modifying
	@Query(value = "update users set leave_balance = leave_balance-1 where uid = ?1"
	, nativeQuery = true)
	void updateLeaveBalance(String uid); //更新用户请假次数-1
}
