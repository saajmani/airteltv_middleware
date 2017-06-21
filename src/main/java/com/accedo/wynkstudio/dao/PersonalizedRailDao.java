package com.accedo.wynkstudio.dao;

import com.accedo.wynkstudio.entity.PersonalizedRail;
import com.accedo.wynkstudio.vo.PersonalizedRailVO;

public interface PersonalizedRailDao {

	public boolean createPersonalizedRail(PersonalizedRail personalizedRail);
	
	public PersonalizedRailVO getPersonalizedRailByUserIdWithRailType(String userId, String railType);
	
	public boolean updatePersonalizedRail(PersonalizedRail personalizedRail);
	
	
	
}
