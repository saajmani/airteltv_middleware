package com.accedo.wynkstudio.util;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;

import java.util.List;

public class JsonTransformation {

	public static String transformJson(String inputJson, String specPath) {

		List<Object> chainrSpecJSON = JsonUtils.classpathToList(specPath);
		Chainr chainr = Chainr.fromSpec(chainrSpecJSON);

		Object inputJSON = JsonUtils.jsonToObject(inputJson);

		Object transformedOutput = chainr.transform(inputJSON);
		System.out.println(JsonUtils.toJsonString(transformedOutput));

		return JsonUtils.toJsonString(transformedOutput);
	}
}