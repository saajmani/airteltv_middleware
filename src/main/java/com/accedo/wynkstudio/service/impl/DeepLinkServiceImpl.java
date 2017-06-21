package com.accedo.wynkstudio.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.accedo.wynkstudio.dao.MaskLinkDao;
import com.accedo.wynkstudio.entity.MaskLink;
import com.accedo.wynkstudio.exception.BusinessApplicationException;
import com.accedo.wynkstudio.helper.AppgridHelper;
import com.accedo.wynkstudio.service.DeepLinkService;
import com.accedo.wynkstudio.vo.MaskLinkVO;
import com.eclipsesource.json.JsonObject;

@Service
public class DeepLinkServiceImpl implements DeepLinkService {

	@Autowired
	private MaskLinkDao maskLinkDao;

	private HttpHeaders headers;
	final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final int RADIX = 36;
	private static final String PIPE = "-";

	@PostConstruct
	public void init() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	}

	@Override
	public String create(String url) {
		String result = "";
		MaskLink maskLink = new MaskLink();
		try {
			MaskLinkVO byUrl = maskLinkDao.getByUrl(url);
			if (byUrl == null) {
				String hashCode = encode(url);
				maskLink.setHashCode(hashCode);
				maskLink.setUrl(url);
				maskLinkDao.create(maskLink);
				result = "{ \"hashCode\": \"" + hashCode + "\"}";
			} else {
				result = "{ \"hashCode\": \"" + byUrl.getHashCode() + "\"}";
			}
		} catch (Exception e) {
			log.error("Error While creating HashCode:", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}

		return result;
	}

	@Override
	public String getAll() {
		List<MaskLinkVO> maskLinkVOs = null;
		try {
			maskLinkVOs = maskLinkDao.getList();
		} catch (Exception e) {
			log.error("Error While getting Mask Url Mapping List:", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}

		return maskLinkVOs.toString();
	}

	@Override
	public String getUrlByHash(String hashCode) {
		MaskLinkVO maskLinkVO = null;
		String url = "";
		try {
			maskLinkVO = maskLinkDao.getByHashCode(hashCode);
			if (maskLinkVO != null) {
				url = maskLinkVO.getUrl();
			}
		} catch (Exception e) {
			log.error("Error While getting URL for Hash:", e);
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}

		return url;
	}

	private String encode(String url) {
		if (StringUtils.isEmpty(url)) {
			throw new BusinessApplicationException(HttpStatus.BAD_REQUEST.value(), "Supplied invalid url: empty");
		}

		String hexValue = Integer.toString(url.hashCode(), RADIX);
		if (hexValue.startsWith(PIPE)) {
			hexValue = hexValue.substring(1);
		}

		return hexValue;
	}

	@Override
	public String getOfferImage(String offerId) {
		JsonObject offerImages = JsonObject.readFrom(AppgridHelper.appGridMetadata.get("offerImages").asString());
		String offerImage = (offerImages.get(offerId) != null && !offerImages.get(offerId).isNull()) ? offerImages.get(
				offerId).asString() : "";

		return offerImage;
	}

}
