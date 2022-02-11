package com.testweb.mall.service;

import com.testweb.mall.dto.UmsMenuNode;
import com.testweb.mall.model.UmsMenu;

import java.util.List;

public interface UmsMenuService {
    /*
    创建后台菜单
     */
    int create(UmsMenu umsMenu);

    /*
    修改后台菜单
     */
    int update(Long id, UmsMenu umsMenu);

    /*
    根据Id获取菜单详情
     */
    UmsMenu getItem(Long id);

    /*
    根据Id删除菜单
     */
    int delete(Long id);

    /*
    分页查询后台菜单
     */
    List<UmsMenu> list(Long parentId, Integer pageSize, Integer pageNum);

    /*
    树形结构返回所有菜单列表
     */
    List<UmsMenuNode> treeList();

    /*
    修改菜单显示状态
     */
    int updateHidden(Long id, Integer hidden);
}
