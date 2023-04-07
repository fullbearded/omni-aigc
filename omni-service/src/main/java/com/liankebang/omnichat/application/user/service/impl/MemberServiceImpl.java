package com.liankebang.omnichat.application.user.service.impl;


import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liankebang.omnichat.application.user.domain.Member;
import com.liankebang.omnichat.application.user.mapper.MemberMapper;
import com.liankebang.omnichat.application.user.service.MemberService;

/**
 * @author: Runner.dada
 * @date: 2020/12/6
 * @description: the system user domain service
 **/
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
}
