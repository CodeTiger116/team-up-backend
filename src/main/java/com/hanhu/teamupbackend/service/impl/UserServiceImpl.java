package com.hanhu.teamupbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hanhu.teamupbackend.common.ErrorCode;
import com.hanhu.teamupbackend.contant.UserConstant;
import com.hanhu.teamupbackend.exception.BusinessException;
import com.hanhu.teamupbackend.mapper.UserMapper;
import com.hanhu.teamupbackend.model.domain.User;
import com.hanhu.teamupbackend.model.vo.UserVO;
import com.hanhu.teamupbackend.service.UserService;
import com.hanhu.teamupbackend.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hanhu.teamupbackend.contant.UserConstant.USER_LOGIN_STATE;

/**
* @author hh
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-03-01 15:44:02
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    //盐值，混淆密码
    private static final String SALT = "hanhu";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //1、校验
        //参数不能为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //参数长度
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }

        //账户不能包含特殊字符，正则表达式匹配
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return -1;
        }
        //密码和校验密码相同
        if(!userPassword.equals(checkPassword)){
            return -1;
        }

        //账户、星球编号 不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
        count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号重复");
        }

        //2、对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3、插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean res = this.save(user);
        if(!res){
            return -1;
        }
        //4、返回新id
        return user.getId();
    }


    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1、校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        //2、查询用户是否存在
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //不存在
        if(user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        //存在
        //3、用户脱敏
        User safetyUser = getSafetyUser(user);

        //4、记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


    /**
     * 用户脱敏
     * @param originUser 传入的用c户
     * @return 脱敏后的用户
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        //去除密码
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     * 根据tag搜索用户（SQL查询）
     * @param tagNameList
     * @return
     */
/*    @Deprecated
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for(String tagName : tagNameList){
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());

    }*/

    /**
     * 根据tag搜索用户（内存查询）
     * @param tagNameList
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //1、先查询所有用户
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2、在内存中判断是否有包含的标签,语法糖,相当于for循环
        return userList.stream().filter(user ->{
            String tagStr = user.getTags();
            Set<String> tempTagNameSet =  gson.fromJson(tagStr,new TypeToken<Set<String>>(){}.getType());
            //判空处理。jdk1.8，如果tempTagNameSet不为空，将值赋给tempTagNameSet，为空就新建一个空对象赋值给tempTagNameSet
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());

    }

    /**
     * 更新用户
     * @param user
     * @param loginUser
     * @return
     */
    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 补充校验，如果用户没有传任何要更新的值，就直接报错，不用执行 update 语句
        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新当前（自己的）信息
        if(!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_ROLE;

    }

    /**
     * 获取匹配的用户
     * @param num
     * @param loginUser
     * @return 脱敏后的用户
     */
    @Override
    public List<User> matchUser(long num, User loginUser) {
        Gson gson = new Gson();
        //所有用户列表，查询tag不为空的
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.ne("tags","[]");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        //当前登录用户的tag,并转换成列表
        String tags = loginUser.getTags();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        //计算所有用户和当前用户的相似度
        //使用列表存储， user==》分数
        List<Pair<User, Long>> list = new ArrayList<>();
        for (User user : userList){
            //比较遍历用户的tag和登录用户的tag
            String userTags = user.getTags();
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            if (StringUtils.isBlank(userTags) || user.getId().equals(loginUser.getId())) {
                continue;
            }
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user,distance));
        }
        //按编辑距离从小到大排序
        List<Pair<User, Long>> topUserPairList = list
                .stream()
                .sorted(Comparator.comparing(Pair::getValue))
                .limit(num)
                .collect(Collectors.toList());
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());

        //按照userIdList中的id顺序，查到User
        String idStr = StringUtils.join(userIdList,",");
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id",userIdList)
                .last("ORDER BY FIELD(id," + idStr + ")");

        //用户脱敏
        return this.list(userQueryWrapper)
                .stream()
                .map(this::getSafetyUser)
                .collect(Collectors.toList());
    }

}




