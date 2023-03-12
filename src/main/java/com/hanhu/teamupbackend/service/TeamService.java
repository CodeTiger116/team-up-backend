package com.hanhu.teamupbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hanhu.teamupbackend.model.domain.Team;
import com.hanhu.teamupbackend.model.domain.User;
import com.hanhu.teamupbackend.model.dto.TeamQuery;
import com.hanhu.teamupbackend.model.request.TeamJoinRequest;
import com.hanhu.teamupbackend.model.request.TeamQuitRequest;
import com.hanhu.teamupbackend.model.request.TeamUpdateRequest;
import com.hanhu.teamupbackend.model.vo.TeamUserVO;

import java.util.List;

/**
* @author hh
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-03-10 00:54:38
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 删除/解散 队伍
     * @param teamId
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long teamId, User loginUser);

    /**
     * 查询队伍列表
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);
}
