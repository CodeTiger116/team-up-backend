package com.hanhu.teamupbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanhu.teamupbackend.model.domain.UserTeam;
import com.hanhu.teamupbackend.mapper.UserTeamMapper;
import com.hanhu.teamupbackend.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author hh
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-03-10 01:00:20
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




