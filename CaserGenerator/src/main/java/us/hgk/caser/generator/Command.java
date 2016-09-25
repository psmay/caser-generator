package us.hgk.caser.generator;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import us.hgk.caser.generator.ConfigModels.Union;

public final class Command {
	private static final Logger log = LoggerFactory.getLogger(Command.class);

	private static final Charset UTF8 = Charset.forName("utf-8");

	public static void main(String[] args) {
		log.info("Loading template from resource");
		String templateSource = readTemplateResource();
		log.info("Compiling template");
		Template template = compileTemplate(templateSource);
		log.info("Template OK");

		log.info("Accepting union spec on stdin...");
		String unionSpecYaml = readUnionSpec(System.in);
		log.info("Parsing and mapping union spec");
		Union unionSpec = parseAndMapUnionSpec(unionSpecYaml);
		log.info("Union spec parsed and mapped OK");

		log.info("Applying template to union spec");
		String result = applyTemplateToUnionSpec(template, unionSpec);
		log.info("Applied template OK");

		System.out.print(result);

		log.info("Done");
	}

	private static String applyTemplateToUnionSpec(Template template, Union unionSpec) {
		String result = null;

		try {
			result = template.apply(unionSpec);
		} catch (IOException e) {
			throw new RuntimeException("I/O error applying template to union spec: " + e.getMessage(), e);
		}
		return result;
	}

	private static void closeLoggingOnError(Closeable toClose, String what) {
		try {
			toClose.close();
		} catch (IOException e) {
			log.error("Error occurred closing " + what + ": " + e.getMessage(), e);
		}
	}

	private static Template compileTemplate(String templateSource) {
		Handlebars handlebars = new Handlebars();

		try {
			return handlebars.compileInline(templateSource);
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse template: " + e.getMessage(), e);
		}
	}

	private static Union parseAndMapUnionSpec(String unionSpecYaml) {
		Union unionSpec = null;

		try {
			unionSpec = ConfigIO.readUnionFrom(unionSpecYaml);
		} catch (JsonParseException e) {
			throw new RuntimeException("Could not parse YAML: " + e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new RuntimeException("Could not map YAML structure to expected format: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException("I/O error parsing union spec:" + e.getMessage(), e);
		}
		return unionSpec;
	}

	private static String readTemplateResource() {
		String templateSource;
		InputStream templateStream = Command.class.getResourceAsStream("/templates/caser-based-class.java.handlebars");

		if (templateStream == null) {
			throw new RuntimeException("Template resource not available");
		}

		templateSource = slurpAndCloseUtf8Stream(templateStream, "template stream");

		return templateSource;
	}

	// Read the YAML union spec from a stream using the default encoding
	private static String readUnionSpec(InputStream src) {
		return slurpAndCloseDefaultStream(src, "union spec");
	}

	private static String slurpAndCloseDefaultStream(InputStream stream, String what) {
		return slurpStreamAndCloseLoggingOnError(stream, null, what);
	}

	private static String slurpAndCloseUtf8Stream(InputStream stream, String what) {
		return slurpStreamAndCloseLoggingOnError(stream, UTF8, what);
	}

	private static String slurpStreamAndCloseLoggingOnError(InputStream stream, Charset charset, String what) {
		try {
			return Slurp.slurpString(stream, charset);
		} catch (IOException e) {
			throw new RuntimeException("I/O error reading " + what + ": " + e.getMessage(), e);
		} finally {
			closeLoggingOnError(stream, what);
		}
	}

	// Prevent instantiation
	private Command() {
	}

}
