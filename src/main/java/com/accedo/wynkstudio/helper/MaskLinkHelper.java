package com.accedo.wynkstudio.helper;

import com.accedo.wynkstudio.entity.MaskLink;
import com.accedo.wynkstudio.vo.MaskLinkVO;

public class MaskLinkHelper {
	
	
	/* Get AppGrid Session Key */
	public static MaskLinkVO toMaskLinkVO(MaskLink maskLink) {
		MaskLinkVO maskLinkVO = new MaskLinkVO();
		maskLinkVO.setHashCode(maskLink.getHashCode());
		maskLinkVO.setId(maskLink.getId());
		maskLinkVO.setUrl(maskLink.getUrl());
		return maskLinkVO;
	}
	
}