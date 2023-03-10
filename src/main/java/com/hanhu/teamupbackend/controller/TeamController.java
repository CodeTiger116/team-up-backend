package com.hanhu.teamupbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanhu.teamupbackend.common.BaseResponse;
import com.hanhu.teamupbackend.common.DeleteRequest;
import com.hanhu.teamupbackend.common.ErrorCode;
import com.hanhu.teamupbackend.common.ResultUtils;
import com.hanhu.teamupbackend.exception.BusinessException;
import com.hanhu.teamupbackend.model.domain.Team;
import com.hanhu.teamupbackend.model.domain.User;
import com.hanhu.teamupbackend.model.dto.TeamQuery;
import com.hanhu.teamupbackend.model.request.TeamAddRequest;
import com.hanhu.teamupbackend.model.request.TeamUpdateRequest;
import com.hanhu.teamupbackend.model.vo.TeamUserVO;
import com.hanhu.teamupbackend.service.TeamService;
import com.hanhu.teamupbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 队伍接口
 *
 * @author hanhu
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    /**
     * 创建队伍
     * @param teamAddRequest 队伍信息
     * @return 创建的队伍id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        //判空
        if(teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        User loginUser = userService.getLoginUser(request);

        long teamId = teamService.addTeam(team,loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 更新队伍
     * @param teamUpdateRequest 更新信息
     * @param request
     * @return 是否成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据id获取单个队伍
     * @param id
     * @return 队伍
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }


    /**
     * 获取全部队伍
     * @param teamQuery 队伍查询封装类
     * @param request
     * @return 队伍和用户信息封装类（脱敏）列表
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        //1、查询队伍列表
        List<TeamUserVO> teamList =teamService.listTeams(teamQuery,isAdmin);
        //2、当前用户是否在队伍


        //3、队伍已有人数
        return ResultUtils.success(teamList);
    }

    /**
     * 分页查询
     * @param teamQuery 队伍查询封装类
     * @return 队伍列表
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //拷贝
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        //分页查询
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = teamService.page(new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize()), queryWrapper);
        return ResultUtils.success(teamPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(){
        return null;
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(){
        return null;
    }


    /**
     * 删除队伍
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }


}
