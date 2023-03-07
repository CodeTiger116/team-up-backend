package com.hanhu.teamupbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hanhu.teamupbackend.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.hanhu.teamupbackend.contant.UserConstant.ADMIN_ROLE;
import static com.hanhu.teamupbackend.contant.UserConstant.USER_LOGIN_STATE;

/**
* @author hh
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-03-01 15:44:02
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @param planetCode 星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     * @param userAccount 账户
     * @param userPassword 密码
     * @param request
     * @return 脱敏后的信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户登出
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 根据tag搜索用户
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 更新用户
     * @param user
     * @param loginUser
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);
    /**
     * 是否为管理员 重载方法
     *
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);
}
