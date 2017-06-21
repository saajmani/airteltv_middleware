package com.accedo.wynkstudio.util;

import org.springframework.http.HttpHeaders;

import com.eclipsesource.json.JsonObject;

public class ContentProviderUtil {

	public static String createPagination(String totalPageSize, String pageSize, String pageNumber,
			String defaultStart, String deafultEnd) {
		String pagination = "";
		int start = 1;
		int end = 20;
		if (totalPageSize != null && !totalPageSize.isEmpty()) {
			if (Integer.parseInt(totalPageSize) > 0 && pageNumber != null && !pageNumber.isEmpty() && pageSize != null
					&& !pageSize.isEmpty() && Integer.parseInt(pageNumber) > 0 && Integer.parseInt(pageSize) > 0) {

				start = Integer.parseInt(pageSize) * (Integer.parseInt(pageNumber) - 1) + 1;
				end = start + Integer.parseInt(pageSize) - 1;
				start = start >= 10000 ? 0  : start; 
				end = start >= 10000 ? 10  :end; 
				pagination = start + "_" + end;//Integer.parseInt(totalPageSize) > start ? start + "_" + end : " ";
			} else {
				pagination = defaultStart + "_" + deafultEnd;
			}
		} else if (pageNumber != null && !pageNumber.isEmpty() && pageSize != null && !pageSize.isEmpty()) {
			start = Integer.parseInt(pageSize) * (Integer.parseInt(pageNumber) - 1) + 1;
			end = start + Integer.parseInt(pageSize) - 1;
			start = start >= 10000 ? 0  : start; 
			end = start >= 10000 ? 10  :end; 
			pagination = start + "_" + end;
		} else {
			pagination = defaultStart + "_" + deafultEnd;
		}
		return pagination;
	}
	
	public static String convertJsonToString(JsonObject jsonObject) {
		try {
			jsonObject.set("favoriteMovies", jsonObject.get("favoriteMovies") !=null && !jsonObject.get("favoriteMovies").isNull() ? jsonObject.get("favoriteMovies").toString() : "[]");
			jsonObject.set("lastWatchedMovies", jsonObject.get("lastWatchedMovies") !=null && !jsonObject.get("lastWatchedMovies").isNull() ? jsonObject.get("lastWatchedMovies").toString() : "[]");
			jsonObject.set("subscribedChannels", jsonObject.get("subscribedChannels") !=null && !jsonObject.get("subscribedChannels").isNull() ? jsonObject.get("subscribedChannels").toString() : "[]");
			jsonObject.set("bundleCounter", jsonObject.get("bundleCounter") !=null && !jsonObject.get("bundleCounter").isNull() ? jsonObject.get("bundleCounter").toString() : "[]");
			jsonObject.set("registeredChannels", jsonObject.get("registeredChannels") !=null && !jsonObject.get("registeredChannels").isNull() ? jsonObject.get("registeredChannels").toString() : "[]");		
		} catch (Exception e) {
		}
		return jsonObject.toString();
	}
	
	public static String getProgramWithAvailableCredits(String response, String url, HttpHeaders headers) {
		/*try {
			String programIds = getAllProgramIds(response);
			String baseURL = url + programIds;
			String creditResponse = Util.executeApiGetCall(baseURL, headers);
			JsonObject convertedJsonObject = JsonObject.readFrom(response);
			JsonArray entries = convertedJsonObject.get("entries").asArray();

			JsonObject creditJsonObject = JsonObject.readFrom(creditResponse);
			JsonArray creditJsonEntries = creditJsonObject.get("entries").asArray();
			JsonObject jsonContent = null;
			JsonObject creditJsonContent = null;
			String programId = null;
			String guId = null;
			int guidPosition = 0;
			JsonArray credits = null;
			JsonObject credit = null;
			if (entries != null && entries.size() > 0) {
				for (int j = 0; j < entries.size(); j++) {
					jsonContent = (JsonObject) entries.get(j);
					credits = new JsonArray();
					for (int i = 0; i < creditJsonEntries.size(); i++) {
						creditJsonContent = creditJsonEntries.get(i).asObject();
						programId = creditJsonContent.get("programId").asString();
						guidPosition = programId.lastIndexOf("/");
						guId = programId.substring(guidPosition + 1, programId.length());
						if (guId.equalsIgnoreCase(jsonContent.get("guid").asString())) {
							credit = new JsonObject();
							credit.add("characterName", creditJsonContent.get("characterName").asString());

							int splitIndex = creditJsonContent.get("personId").asString().lastIndexOf("/");
							String personName = creditJsonContent.get("personId").asString().substring(splitIndex + 1);
							personName = personName.replace("+", " ");

							credit.add("personName", personName);
							credit.add("creditType", creditJsonContent.get("creditType").asString());
							credits.add(credit);
						}
					}
					jsonContent.set("credits", credits);
					entries.set(j, jsonContent);
				}

				convertedJsonObject.set("entries", entries);
				response = convertedJsonObject.toString();
			}
		} catch (Exception e) {
			throw new BusinessApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}*/
		return response;

	}
}
