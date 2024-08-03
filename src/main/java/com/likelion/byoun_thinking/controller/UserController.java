package com.likelion.byoun_thinking.controller;

import com.likelion.byoun_thinking.api.UnivCert;
import com.likelion.byoun_thinking.dto.*;
import com.likelion.byoun_thinking.entity.User;
import com.likelion.byoun_thinking.service.ChallengeService;
import com.likelion.byoun_thinking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ChallengeService challengeService;

    // 이메일 인증 번호 발송
    @GetMapping("/user/emailAuthReq")
    public Map<String, Object> emailAuthReq(@RequestParam String email, @RequestParam String universityName) {
        try {
            String API_KEY = "2a9a8881-5573-419a-8d7b-c6a7e8821e9e";
            boolean univ_check = true;
            log.info("들어온 대학교 : " + universityName);
            // UnivCert.certify()의 결과에서 필요한 데이터를 추출
            Map<String, Object> result = UnivCert.certify(API_KEY, email, universityName, univ_check);
            boolean success = (boolean) result.get("success");

            // 성공 여부에 따라 응답 구성
            return Map.of("success", success);
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "error");
        }
    }

    // 이메일 인증 번호 확인
    @GetMapping("/user/emailAuthRes")
    public Map<String, Object> emailAuthRes(@RequestParam String email, @RequestParam String universityName, @RequestParam int code) {
        try {
            String API_KEY = "2a9a8881-5573-419a-8d7b-c6a7e8821e9e";

            Map<String, Object> result = UnivCert.certifyCode(API_KEY, email, universityName, code);
            boolean success = (boolean) result.get("success");

            return Map.of("success", success);
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "error");
        }
    }

    @GetMapping("/user/clear")
    public Map<String, Object> clear() {
        try {
            String API_KEY = "2a9a8881-5573-419a-8d7b-c6a7e8821e9e";

            Map<String, Object> result = UnivCert.clear(API_KEY);
            boolean success = (boolean) result.get("success");

            return Map.of("success", success);
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "error");
        }
    }

    @GetMapping("/user/clear")
    public Map<String, Object> list() {
        try {
            String API_KEY = "2a9a8881-5573-419a-8d7b-c6a7e8821e9e";

            Map<String, Object> result = UnivCert.list(API_KEY);
            boolean success = (boolean) result.get("success");

            return Map.of("success", success);
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "error");
        }
    }

    @PostMapping("/user/signUp")
    public ResponseEntity<MessageDTO> signUp(@Valid @RequestBody UserSignUpRequestDTO request, BindingResult bindingResult) {
        MessageDTO messageDTO = new MessageDTO();

        if (bindingResult.hasErrors()) { // 입력받은 request body 값에 정상적인 값들이 들어있는지 확인
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError error : list) {
                messageDTO.setMessage(error.getDefaultMessage());
                return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
            }
        }

        boolean isDuplicated = userService.checkEmailDuplication(request.getEmail()); // 아이디 중복 체크

        if (isDuplicated) {
            messageDTO.setMessage("중복된 아이디입니다.");
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.signUp(request); // 중복되지 않았으면 계정 생성
            messageDTO.setMessage("회원가입 성공");
            return new ResponseEntity<>(messageDTO, HttpStatus.CREATED);
        }catch(IllegalArgumentException e){
            messageDTO.setMessage("학교 정보가 잘못되었습니다.");
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<MessageDTO> login(@Valid @RequestBody UserLoginRequestDTO request, HttpServletRequest httpServletRequest, BindingResult bindingResult) {
        MessageDTO messageDTO = new MessageDTO();

        if (bindingResult.hasErrors()) { // 입력받은 request body 값에 정상적인 값들이 들어있는지 확인
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError error : list) {
                messageDTO.setMessage(error.getDefaultMessage());
                return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
            }
        }

        User user = userService.login(request); // 로그인 성공시 User 객체 리턴 or 실패시 null 리턴

        if (user == null) {
            messageDTO.setMessage("로그인 실패");
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }

        httpServletRequest.getSession().invalidate(); // 기존 세션 무효화
        HttpSession session = httpServletRequest.getSession(true); // 세션을 있으면 가져오고 없으면 생성해서 리턴
        session.setAttribute("userId", user.getUserId()); // 세션에 userId 등록
        session.setMaxInactiveInterval(1800); // 세션 기한 30분 설정
        messageDTO.setMessage("로그인 성공");
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @PostMapping("/user/logout")
    public ResponseEntity<MessageDTO> logout(HttpServletRequest httpServletRequest) {
        MessageDTO messageDTO = new MessageDTO();
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null) {
            messageDTO.setMessage("잘못된 접근입니다.");
            return new ResponseEntity<>(messageDTO, HttpStatus.UNAUTHORIZED);
        }
        session.invalidate(); // 세션 정보 삭제

        messageDTO.setMessage("로그아웃 성공");
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    // List<DTO> => Map<>
    private Map<String, List<ChallengeInfoDTO>> getStringListMap(List<ChallengeInfoDTO> userInfo) {
        List<ChallengeInfoDTO> collect = userInfo.stream()
                .map(challenge -> new ChallengeInfoDTO(
                        challenge.getId(),
                        challenge.getTitle(),
                        challenge.getDescription(),
                        challenge.getInCount()
                ))
                .collect(Collectors.toList());

        Map<String, List<ChallengeInfoDTO>> response = new HashMap<>();
        response.put("challenge", collect);
        return response;
    }

    @GetMapping("/user/userInfo")
    public Map<String, List<UserInfoDTO>> getUserInfo(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not Login or session expired : getUserInfo method");
        }
        Integer userId = (Integer) session.getAttribute("userId");
        List<UserInfoDTO> userInfo = userService.getUserInfo(userId);
        
        List<UserInfoDTO> collect = userInfo.stream()
                .map(user -> new UserInfoDTO(
                        user.getName(),
                        user.getEmail(),
                        user.getSchoolName(),
                        user.getImageUrl()
                ))
                .collect(Collectors.toList());

        Map<String, List<UserInfoDTO>> response = new HashMap<>();
        response.put("info", collect);
        return response;
    }

    @GetMapping("/user/inSchoolChal")
    public Map<String, List<ChallengeInfoDTO>> getMySchoolChallengeInfo(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not Login or session expired : getUserInfo method");
        }
        Integer userId = (Integer) session.getAttribute("userId");
        List<ChallengeInfoDTO> userInfo = challengeService.getMySchoolChallengeInfo(userId);

        return getStringListMap(userInfo);
    }

    @GetMapping("/user/inAllChal")
    public Map<String, List<ChallengeInfoDTO>> getMyAllChallengeInfo(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not Login or session expired : getMyAllChallengeInfo method");
        }
        Integer userId = (Integer) session.getAttribute("userId");
        List<ChallengeInfoDTO> userInfo = challengeService.getMyAllChallengeInfo(userId);

        return getStringListMap(userInfo);
    }
}
