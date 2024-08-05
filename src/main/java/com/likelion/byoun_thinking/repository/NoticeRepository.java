package com.likelion.byoun_thinking.repository;

import com.likelion.byoun_thinking.dto.NoticeInfoDTO;
import com.likelion.byoun_thinking.dto.NoticeListDTO;
import com.likelion.byoun_thinking.entity.Notice;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    @Query("SELECT new com.likelion.byoun_thinking.dto.NoticeListDTO(n.noticeId, n.title, n.important, n.modifiedAt) " +
            "FROM Notice n ORDER BY n.important DESC, n.modifiedAt DESC")
    List<NoticeListDTO> findAllSortedByImportant();

    @Query("SELECT new com.likelion.byoun_thinking.dto.NoticeInfoDTO(n.noticeId, n.title, n.content, n.important, n.modifiedAt) " +
            "FROM Notice n WHERE n.noticeId = :noticeId")
    List<NoticeInfoDTO> findNoticeByNoticeId(@Param("noticeId") Integer noticeId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO notice (user_id, title, content, important, modified_at) " +
            "VALUES (:userId, :title, :content, :important, CURRENT_TIMESTAMP)", nativeQuery = true)
    Integer putNotice(@Param("userId") Integer userId, @Param("title") String title, @Param("content") String content, @Param("important") boolean important);

    @Transactional
    @Modifying
    @Query(value = "UPDATE notice SET title = :title, content = :content, important = :important, modified_at = CURRENT_TIMESTAMP " +
            "WHERE notice_Id = :noticeId", nativeQuery = true)
    Integer updateNotice(@Param("noticeId") Integer noticeId, @Param("title") String title, @Param("content") String content, @Param("important") boolean important);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM notice WHERE notice_id = :noticeId", nativeQuery = true)
    Integer deleteNotice(@Param("noticeId") Integer noticeId);
}
