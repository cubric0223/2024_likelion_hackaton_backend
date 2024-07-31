package com.likelion.byoun_thinking.service;

import com.likelion.byoun_thinking.dto.NoticeInfoDTO;
import com.likelion.byoun_thinking.dto.NoticeListDTO;
import com.likelion.byoun_thinking.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    // 공지사항을 목록을 불러오는 함수(중요 공지사항은 위로)
    public List<NoticeListDTO> getNotice() {
        return noticeRepository.findAllSortedByImportant();
    }

    // 공지사항 자세한 정보 가져온다
    public List<NoticeInfoDTO> getNoticeInfo(Integer noticeId) {
        return noticeRepository.findNoticeByNoticeId(noticeId);
    }

    // 공지사항 등록
    public Integer postNotice(Integer userId, String title, String content, Boolean important) {
        return noticeRepository.putNotice(userId, title, content, important);
    }

    // 공지사항 수정
    public Integer updateNotice(Integer noticeId, String title, String content, Boolean important) {
        return noticeRepository.updateNotice(noticeId, title, content, important);
    }

    public Integer deleteNotice(Integer noticeId) {
        return noticeRepository.deleteNotice(noticeId);
    }
}
