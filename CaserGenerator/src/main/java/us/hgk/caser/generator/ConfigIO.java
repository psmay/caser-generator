package us.hgk.caser.generator;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import us.hgk.caser.generator.ConfigModels.Union;

class ConfigIO {
	private static ObjectMapper createMapper() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper;
	}

	private static ObjectMapper mapper = createMapper();

	public static Union readUnionFrom(String yamlSource) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(yamlSource, Union.class);
	}

	public static String writeUnionToString(Union thing) throws JsonProcessingException {
		return mapper.writeValueAsString(thing);
	}
}
