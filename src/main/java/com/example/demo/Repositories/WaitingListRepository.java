package com.example.demo.Repositories;

import com.example.demo.Entities.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingList, Integer> { // Sử dụng Integer cho Khóa chính ID hàng đợi

    // Hàm chuẩn Spring Data JPA bóc tách quan hệ: Tìm danh sách chờ theo Mã lịch trình và xếp thứ tự đăng ký trước lên đầu (FIFO)
    List<WaitingList> findByDoctorSchedule_ScheduleIdOrderByCreatedAtAsc(Integer scheduleId);
}