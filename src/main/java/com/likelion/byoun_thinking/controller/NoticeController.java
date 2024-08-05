package com.likelion.byoun_thinking.controller;

import com.likelion.byoun_thinking.dto.NoticeInfoDTO;
import com.likelion.byoun_thinking.dto.NoticeListDTO;
import com.likelion.byoun_thinking.dto.NoticeRequestDTO;
import com.likelion.byoun_thinking.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController()
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/notices/list")
    public Map<String, List<NoticeListDTO>> getNoticeList(){
        List<NoticeListDTO> notices = noticeService.getNotice();

        List<NoticeListDTO> collect = notices.stream().map(
                notice -> new NoticeListDTO(
                        notice.getNoticeId(),
                        notice.getTitle(),
                        notice.getImportant(),
                        notice.getModifiedAt()
                )).collect(Collectors.toList());
        Map<String, List<NoticeListDTO>> response = new HashMap<>();
        response.put("notices", collect);
        return response;
    }

    @GetMapping("/notices/info")
    public Map<String, List<NoticeInfoDTO>> getNoticeList(@RequestParam Integer noticeId){
        List<NoticeInfoDTO> notices = noticeService.getNoticeInfo(noticeId);

        List<NoticeInfoDTO> collect = notices.stream().map(
                notice -> new NoticeInfoDTO(
                        notice.getNoticeId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getImportant(),
                        notice.getModifiedAt()
                )).collect(Collectors.toList());
        Map<String, List<NoticeInfoDTO>> response = new HashMap<>();
        response.put("notices", collect);
        return response;
    }

    @PostMapping("/notices/create")
    public Integer postNotice(
            @RequestBody NoticeRequestDTO noticeRequestDTO,
            HttpServletRequest httpServletRequest){

        // 로그인이 안되어 있는 경우
        HttpSession session = httpServletRequest.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not Login or session expired : postNotice method");
        }

        Integer userId = (Integer) session.getAttribute("userId");

        // 제목 내용이 빈 경우
        if (noticeRequestDTO.getTitle() == null || noticeRequestDTO.getTitle().trim().isEmpty() ||
                noticeRequestDTO.getContent() == null || noticeRequestDTO.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title and content cannot be empty.");
        }

        int result = noticeService.postNotice(userId, noticeRequestDTO.getTitle(),
                noticeRequestDTO.getContent(), noticeRequestDTO.getImportant());

        if (result == 1) {
            // 성공
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Notice upload success"))
                    .getStatusCodeValue();
        } else {
            // 실패
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Notice upload fail"))
                    .getStatusCodeValue();
        }
    }

    @PutMapping("/notices/update")
    public Integer updateNotice(
            @RequestBody NoticeRequestDTO noticeRequestDTO,
            HttpServletRequest httpServletRequest){

        // 로그인이 안되어 있는 경우
        HttpSession session = httpServletRequest.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not Login or session expired : postNotice method");
        }

        Integer userId = (Integer) session.getAttribute("userId");

        // 제목 내용이 빈 경우
        if (noticeRequestDTO.getTitle() == null || noticeRequestDTO.getTitle().trim().isEmpty() ||
                noticeRequestDTO.getContent() == null || noticeRequestDTO.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title and content cannot be empty.");
        }

        int result = noticeService.updateNotice(userId, noticeRequestDTO.getTitle(),
                noticeRequestDTO.getContent(), noticeRequestDTO.getImportant());

        if (result == 1) {
            // 성공
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Notice update success"))
                    .getStatusCodeValue();
        } else {
            // 실패
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Notice update fail"))
                    .getStatusCodeValue();
        }
    }

    @DeleteMapping("/notices/delete")
    public Integer deleteNotice(@RequestParam Integer noticeId){
        int result = noticeService.deleteNotice(noticeId);
        if (result == 1) {
            // 성공
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Notice upload success"))
                    .getStatusCodeValue();
        } else {
            // 실패
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Notice upload fail"))
                    .getStatusCodeValue();
        }
    }
}
