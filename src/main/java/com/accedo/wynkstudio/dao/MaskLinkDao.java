package com.accedo.wynkstudio.dao;

import java.util.List;

import com.accedo.wynkstudio.entity.MaskLink;
import com.accedo.wynkstudio.vo.MaskLinkVO;

public interface MaskLinkDao {

	public boolean create(MaskLink masterLinking);
	
	public MaskLinkVO getByHashCode(String hashCode);
	
	public List<MaskLinkVO> getList();

	public MaskLinkVO getByUrl(String url);
	
}
