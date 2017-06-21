package com.accedo.wynkstudio.dao;

import java.util.List;

import com.accedo.wynkstudio.entity.UserFilter;
import com.accedo.wynkstudio.vo.UserFilterVO;

public interface UserFilterDao {

	public String createUserFilter(UserFilter userFilter);

	public String deleteUserFilter(String uId, String filterName);

	public String updateUserFilter(String uId, String filterName, String oldName);

	public List<UserFilterVO> getUserFilterUId(String uId);

	public int getUserFilterCountByUserId(String uId, String filtertype);

	int getUserFilterNameCount(String userId, String filterName, String filtertype);

	public UserFilterVO getUserFilterByFilterDetails(String uId, String filter, String filtertype, String sortingKey, String languages);

}
