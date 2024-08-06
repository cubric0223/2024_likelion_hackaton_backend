package com.likelion.byoun_thinking.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.likelion.byoun_thinking.dto.*;
import com.likelion.byoun_thinking.service.ChallengeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChallengeController {
    private final AmazonS3 amazonS3Client;
    private final ChallengeService challengeService;
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping("/chal")
    public ResponseEntity<MessageDTO> createChallenge(@Valid @RequestBody CreateChallengeRequestDTO requestDTO,
                                                      HttpServletRequest httpServletRequest ,BindingResult bindingResult) {
        HttpSession session = httpServletRequest.getSession(false);
        MessageDTO messageDTO = new MessageDTO();

        if(session == null){
            messageDTO.setMessage("챌린지 생성 실패");
            return new ResponseEntity<>(messageDTO, HttpStatus.UNAUTHORIZED);
        }

        Integer user_id = Integer.parseInt(session.getAttribute("userId").toString());

        if (bindingResult.hasErrors()) { // 입력받은 request body 값에 정상적인 값들이 들어있는지 확인
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError error : list) {
                messageDTO.setMessage(error.getDefaultMessage());
                return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
            }
        }

        challengeService.createChallenge(requestDTO, user_id);
        messageDTO.setMessage("챌린지 생성 성공");
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @PostMapping("/chal/{chal_id}")
    public ResponseEntity<MessageDTO> joinChallenge(@PathVariable Integer chal_id, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        MessageDTO messageDTO = new MessageDTO();

        if(session == null){
            messageDTO.setMessage("참여 신청 실패");
            return new ResponseEntity<>(messageDTO, HttpStatus.UNAUTHORIZED);
        }

        final String userId = session.getAttribute("userId").toString();
        challengeService.joinChallenge(chal_id,Integer.parseInt(userId));

        messageDTO.setMessage("참여 신청 성공");
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @GetMapping("/chal/{chal_id}")
    public ResponseEntity<ChallengeDetailDTO> challengeDetail(@PathVariable Integer chal_id, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if(session == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Integer userId = Integer.parseInt(session.getAttribute("userId").toString());

        ChallengeDetailDTO challengeDetailDTO = challengeService.challengeDetail(userId, chal_id);

        return new ResponseEntity<>(challengeDetailDTO, HttpStatus.OK);
    }

    @PostMapping("/chal/{chal_id}/auth/0")
    public ResponseEntity<MessageDTO> authChallengeWithImage(@PathVariable Integer chal_id,@RequestParam("image_file") MultipartFile image_file, @RequestParam("content") String content,
                                           HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        MessageDTO messageDTO = new MessageDTO();

        if(session == null){
            messageDTO.setMessage("인증 실패");
            return new ResponseEntity<>(messageDTO, HttpStatus.UNAUTHORIZED);
        }

        Integer userId = Integer.parseInt(session.getAttribute("userId").toString());

        challengeService.joinChallenge(chal_id,userId);

        String fileUrl;
        try{
            String fileName = image_file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image_file.getContentType());
            metadata.setContentLength(image_file.getSize());

            // 파일 업로드
            amazonS3Client.putObject(bucket, fileName, image_file.getInputStream(), metadata);
            // 파일 URL
            fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();

        }catch (IOException e){
            logger.error("Error uploading file: {}", image_file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if(!challengeService.authChallengeWithImage(new ChallengeImageAuthRequestDTO(fileUrl, content), userId, chal_id)){
            messageDTO.setMessage("인증 실패");
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }

        messageDTO.setMessage("인증 성공");
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @PostMapping("/chal/{chal_id}/auth/1")
    public ResponseEntity<MessageDTO> authChallengeWithText(@PathVariable Integer chal_id, @Valid @RequestBody ChallengeTextAuthRequestDTO requestDTO,
                                                            HttpServletRequest httpServletRequest,BindingResult bindingResult){
        HttpSession session = httpServletRequest.getSession(false);
        MessageDTO messageDTO = new MessageDTO();

        if (bindingResult.hasErrors()) { // 입력받은 request body 값에 정상적인 값들이 들어있는지 확인
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError error : list) {
                messageDTO.setMessage(error.getDefaultMessage());
                return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
            }
        }

        if(session == null){
            messageDTO.setMessage("인증 실패");
            return new ResponseEntity<>(messageDTO, HttpStatus.UNAUTHORIZED);
        }

        Integer userId = Integer.parseInt(session.getAttribute("userId").toString());

        if(!challengeService.authChallengeWithText(requestDTO, userId, chal_id)){
            messageDTO.setMessage("인증 실패");
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }

        messageDTO.setMessage("인증 성공");
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }
}
