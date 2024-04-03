package org.example.cafeflow.Member.service;

import org.example.cafeflow.Member.domain.City;
import org.example.cafeflow.Member.domain.Member;
import org.example.cafeflow.Member.domain.State;
import org.example.cafeflow.Member.dto.MemberDto;
import org.example.cafeflow.Member.dto.MemberLoginDto;
import org.example.cafeflow.Member.dto.MemberRegistrationDto;
import org.example.cafeflow.Member.dto.TokenDto;
import org.example.cafeflow.Member.repository.CityRepository;
import org.example.cafeflow.Member.repository.MemberRepository;
import org.example.cafeflow.Member.repository.StateRepository;
import org.example.cafeflow.Member.util.JwtTokenProvider;
import org.example.cafeflow.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public TokenDto registerMember(MemberRegistrationDto registrationDto) {
        memberRepository.findByLoginId(registrationDto.getLoginId()).ifPresent(m -> {
            throw new DuplicateLoginIdException("이미 등록된 ID 입니다.");
        });

        memberRepository.findByEmail(registrationDto.getEmail()).ifPresent(m -> {
            throw new DuplicateEmailException("이미 등록된 이메일입니다.");
        });

        memberRepository.findByNickname(registrationDto.getNickname()).ifPresent(m -> {
            throw new DuplicateNicknameException("이미 등록된 닉네임입니다.");
        });

        State state = stateRepository.findById(registrationDto.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID를 가진 '도/시'가 없습니다: " + registrationDto.getStateId()));
        City city = cityRepository.findById(registrationDto.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID를 가진 '시/군,구'가 없습니다: " + registrationDto.getCityId()));


        Member.UserType userType = registrationDto.getUserType() != null ? registrationDto.getUserType() : Member.UserType.USER;
        Member member = Member.builder()
                .username(registrationDto.getUsername())
                .nickname(registrationDto.getNickname())
                .loginId(registrationDto.getLoginId())
                .passwordHash(passwordEncoder.encode(registrationDto.getPassword()))
                .email(registrationDto.getEmail())
                .gender(registrationDto.getGender())
                .age(registrationDto.getAge())
                .userType(userType)
                .state(state)
                .city(city)
                .build();

        memberRepository.save(member);
        String token = jwtTokenProvider.createToken(member.getLoginId(), member.getUserType());

        return new TokenDto(token);
    }

    public TokenDto loginMember(MemberLoginDto loginDto) {
        Member member = memberRepository.findByLoginId(loginDto.getLoginId())
                .orElseThrow(() -> new UserNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPasswordHash())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(member.getLoginId(), member.getUserType());
        return new TokenDto(token);
    }

    public MemberDto getMemberDetailsByUsername(String username) {
        return memberRepository.findByLoginId(username)
                .map(member -> new MemberDto(
                        member.getId(),
                        member.getUsername(),
                        member.getNickname(),
                        member.getEmail(),
                        member.getGender(),
                        member.getAge(),
                        member.getCity() != null ? member.getCity().getId() : null,
                        member.getState() != null ? member.getState().getId() : null
                ))
                .orElse(null);
    }

}