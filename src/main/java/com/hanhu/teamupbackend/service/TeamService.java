package com.hanhu.teamupbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hanhu.teamupbackend.model.domain.Team;
import com.hanhu.teamupbackend.model.domain.User;
import com.hanhu.teamupbackend.model.request.TeamUpdateRequest;

/**
* @author hh
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-03-10 00:54:38
*/
public interface TeamService extends IService<Team> {

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);
}
