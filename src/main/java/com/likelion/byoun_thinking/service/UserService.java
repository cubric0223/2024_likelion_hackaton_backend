package com.likelion.byoun_thinking.service;

import com.likelion.byoun_thinking.dto.UserInfoDTO;
import com.likelion.byoun_thinking.entity.User;
import com.likelion.byoun_thinking.dto.UserLoginRequestDTO;
import com.likelion.byoun_thinking.dto.UserSignUpRequestDTO;
import com.likelion.byoun_thinking.repository.SchoolRepository;
import com.likelion.byoun_thinking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;

    // 이메일 중복 검사 함수
    public boolean checkEmailDuplication(String email){
        return userRepository.existsByEmail(email);
    }

    // 회원가입 함수
    @Transactional
    public void signUp(UserSignUpRequestDTO request) throws IllegalArgumentException {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(hashingPassword(request.getPassword()));
        user.setName(request.getName());
        user.setSchool(schoolRepository.findByName(request.getSchool())
                .orElseThrow(()-> new IllegalArgumentException("school doesn't exits"))
        );
        userRepository.save(user);
    }

    // 로그인 함수
    public User login(UserLoginRequestDTO request){
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if(optionalUser.isEmpty()){
            return null;
        }

        User user = optionalUser.get();

        if(BCrypt.checkpw(request.getPassword(),user.getPassword())){ // DB 비밀번호와 입력받은 비밀번호 확인
            return user;
        }else{
            return null;
        }
    }

    // fileUrl 업로드 함수
    public void updateImageUrl(Integer userId, String imageUrl) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setImageUrl(imageUrl);
        userRepository.save(user);
    }

    // userInfo 조회
    public List<UserInfoDTO> getUserInfo(Integer userId) {
        return userRepository.getUserInfoByUserId(userId);
    }

    // 비밀번호 해싱화
    public String hashingPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
