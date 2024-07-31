package com.likelion.byoun_thinking.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.likelion.byoun_thinking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
public class FileController {
    private final AmazonS3 amazonS3Client;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public FileController(AmazonS3 amazonS3Client, UserService userService) {
        this.amazonS3Client = amazonS3Client;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             HttpServletRequest httpServletRequest) {
        // 가져온 세션에서 userId 없으면 에러
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not login or session expired");
        }
        Integer userId = (Integer) session.getAttribute("userId");

        try {
            String fileName = file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 파일 업로드
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            // 파일 URL
            String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();

            userService.updateImageUrl(userId, fileUrl);
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            logger.error("Error uploading file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
