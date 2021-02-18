package com.zfans.shenlan.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zfans.shenlan.common.base.result.R;
import com.zfans.shenlan.service.edu.entity.Teacher;
import com.zfans.shenlan.service.edu.entity.vo.TeacherQueryVo;
import com.zfans.shenlan.service.edu.feign.OssFileService;
import com.zfans.shenlan.service.edu.mapper.TeacherMapper;
import com.zfans.shenlan.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author Zfans
 * @since 2021-01-25
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private OssFileService ossFileService;

    @Override
    public List<Map<String, Object>> selectNameListByKey(String key) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name");
        queryWrapper.likeRight("name", key);

        List<Map<String, Object>> list = baseMapper.selectMaps(queryWrapper);//返回值是Map列表
        return list;
    }

    @Override
    public boolean removeAvatarById(String id) {

        Teacher teacher = baseMapper.selectById(id);
        if (teacher != null) {
            String avatar = teacher.getAvatar();
            if (!StringUtils.isEmpty(avatar)) {
                //删除图片
                R r = ossFileService.removeFile(avatar);
                return r.getSuccess();
            }
        }
        return false;
    }

    @Override
    public IPage<Teacher> selectPage(Page<Teacher> pageParam, TeacherQueryVo teacherQueryVo) {

        // 显示分页查询列表
        // 1、排序：按照 sort 字段排序
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        // 2、分页查询
        if (teacherQueryVo == null) {
            return baseMapper.selectPage(pageParam, queryWrapper);
        }
        // 3、条件查询
        String name = teacherQueryVo.getName();
        Integer level = teacherQueryVo.getLevel();
        String begin = teacherQueryVo.getJoinDateBegin();
        String end = teacherQueryVo.getJoinDateEnd();

        if (StringUtils.hasText(name)) {
            //左%会使索引失效
            queryWrapper.likeRight("name", name);
        }

        if (level != null) {
            queryWrapper.eq("level", level);
        }

        if (StringUtils.hasText(begin)) {
            queryWrapper.ge("join_date", begin);
        }

        if (StringUtils.hasText(end)) {
            queryWrapper.le("join_date", end);
        }

        return baseMapper.selectPage(pageParam, queryWrapper);
    }
}
