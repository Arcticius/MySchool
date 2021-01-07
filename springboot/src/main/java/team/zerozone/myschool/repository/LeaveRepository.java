package team.zerozone.myschool.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import team.zerozone.myschool.entity.LeaveInfo;

public interface LeaveRepository extends JpaRepository<LeaveInfo, Integer>{
	
	@Query("select l.leaveId from LeaveInfo l where uid = :#{#leaveInfo.uid} and status = :#{#leaveInfo.status}")
	Integer findIdForAuditingByUid(LeaveInfo leaveInfo); //查找该uid下所有处于审核状态的请假申请
	
	@Query("select l from LeaveInfo l where leaveId = ?1")
	LeaveInfo findLeaveInfoById(Integer leaveId); //根据leave_id查找请假申请

	@Query("select l from LeaveInfo l where uid = ?1")
	List<LeaveInfo> findInfosByUid(String uid); //根据uid查找所有请假申请
	
	@Transactional
	@Modifying
	@Query(value = "insert into leave_info(uid, leave_date, reason, status) "
			+ "values(:#{#leaveInfo.uid}, :#{#leaveInfo.leaveDate}, :#{#leaveInfo.reason}, :#{#leaveInfo.status})"
			, nativeQuery = true)
	void insertNewLeaveInfo(LeaveInfo leaveInfo); //插入新的请假申请数据
	
	@Query(value = "select * from leave_info where status = 'auditing'"
			, nativeQuery = true)
	List<LeaveInfo> findAllAuditingInfos(); //查找所有处于审核状态的请假申请
	
	@Transactional
	@Modifying
	@Query(value = "update leave_info set status = ?1 where leave_id = ?2"
	, nativeQuery = true)
	void updateLeaveApply(String status, Integer leaveId); //更新请假申请的状态信息
}
