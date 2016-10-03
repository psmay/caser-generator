package us.hgk.caser.generator;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigModels {
	private static final Logger log = LoggerFactory.getLogger(ConfigModels.class);
	
	public static class Union {
		private String packageName;
		private String name;
		private String doc;
		private String modifiers;
		private Case[] cases;
		private boolean excludeDefaultHandlers = false;
		private Handler[] handlers;

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDoc() {
			return doc;
		}

		public void setDoc(String doc) {
			this.doc = doc;
		}

		public String getModifiers() {
			return modifiers;
		}

		public void setModifiers(String modifiers) {
			this.modifiers = modifiers;
		}

		public Case[] getCases() {
			return cases;
		}

		public void setCases(Case[] cases) {
			this.cases = cases;
		}

		public Handler[] getHandlers() {
			return handlers;
		}

		public void setHandlers(Handler[] handlers) {
			this.handlers = handlers;
		}

		public boolean getExcludeDefaultHandlers() {
			return excludeDefaultHandlers;
		}

		public void setExcludeDefaultHandlers(boolean excludeDefaultHandlers) {
			this.excludeDefaultHandlers = excludeDefaultHandlers;
		}

		@Override
		public String toString() {
			return "Union [packageName=" + packageName + ", name=" + name + ", doc=" + doc + ", modifiers=" + modifiers
					+ ", cases=" + Arrays.toString(cases) + ", excludeDefaultHandlers=" + excludeDefaultHandlers
					+ ", handlers=" + Arrays.toString(handlers) + "]";
		}
	}

	public static class Case {
		private String name;
		private String doc;
		private Parameter[] parameters;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDoc() {
			return doc;
		}

		public void setDoc(String doc) {
			this.doc = doc;
		}

		public Parameter[] getParameters() {
			return parameters;
		}

		public void setParameters(Parameter[] parameters) {
			this.parameters = parameters;
		}

		@Override
		public String toString() {
			return "Case [name=" + name + ", doc=" + doc + ", parameters=" + Arrays.toString(parameters) + "]";
		}
	}

	public static class Parameter {
		private String name;
		private String type;
		private String doc;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDoc() {
			return doc;
		}

		public void setDoc(String doc) {
			this.doc = doc;
		}

		@Override
		public String toString() {
			return "Parameter [name=" + name + ", type=" + type + ", doc=" + doc + "]";
		}
	}

	public static class EnglishListItem {
		private String prefix;
		private String phrase;
		private String suffix;

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getPhrase() {
			return phrase;
		}

		public void setPhrase(String phrase) {
			this.phrase = phrase;
		}

		public String getSuffix() {
			return suffix;
		}

		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}

		public static EnglishListItem[] createListItems(String[] inputItems, String conjunction) {
			if (inputItems == null) {
				return null;
			}

			EnglishListItem[] results = new EnglishListItem[inputItems.length];
			int i = -1;
			for (String inputItem : inputItems) {
				++i;
				EnglishListItem newItem = new EnglishListItem();
				newItem.phrase = inputItem;
				newItem.suffix = ", ";
				results[i] = newItem;
			}

			// Never any comma after the last item
			results[results.length - 1].suffix = null;

			// A conjunction before the last item if there are multiple items
			if (results.length > 1 && conjunction != null) {
				results[results.length - 1].prefix = conjunction + " ";
			}

			// Drop the comma before the conjunction if there are only two items
			if (results.length == 2 && conjunction != null) {
				results[0].suffix = " ";
			}

			return results;
		}
	}

	public enum GenericVariance {
		NONE, EXTENDS, SUPER;

		public String getLabel() {
			return this.name().toLowerCase();
		}

		public static GenericVariance fromLabel(String label) {
			String uc = (label == null) ? null : label.toUpperCase();
			return GenericVariance.valueOf(uc);
		}
	}

	enum NonObjectReturnType {
		VOID(null), BOOLEAN("false"), BYTE("((byte)0)"), CHAR("((char)0)"), SHORT("((short)0)"), INT("0"), LONG(
				"0L"), FLOAT("0.0f"), DOUBLE("0.0");

		private final String defaultValueExpression;

		private NonObjectReturnType(String defaultValueExpression) {
			this.defaultValueExpression = defaultValueExpression;
		}

		public boolean isVoid() {
			return this.equals(VOID);
		}

		public String getDefaultValueExpression() {
			return defaultValueExpression;
		}

		public static NonObjectReturnType valueOf(String name, boolean caseSensitive) {
			if (!caseSensitive) {
				name = name.toUpperCase();
			}
			return valueOf(name);
		}

		public String typeName() {
			return name().toLowerCase();
		}
		
		public static NonObjectReturnType valueOfLower(String lowerCaseName) {
			if (!lowerCaseName.toLowerCase().equals(lowerCaseName)) {
				throw new IllegalArgumentException("Given name must be lower-case");
			}
			return valueOf(lowerCaseName, false);
		}
	}

	public static class Handler {
		private String name;
		private String doc;
		private String returns;
		private boolean returnsGeneric = false;
		private String[] throws_;
		private String default_;
		
		public Handler() {
			log.info("Handler being created");
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDoc() {
			return doc;
		}

		public void setDoc(String doc) {
			this.doc = doc;
		}

		private NonObjectReturnType getNonObjectReturnType() {
			return getNonObjectReturnType(returns);
		}

		private static NonObjectReturnType getNonObjectReturnType(String returnTypeName) {
			if (returnTypeName == null) {
				return NonObjectReturnType.VOID;
			}

			try {
				return NonObjectReturnType.valueOfLower(returnTypeName);
			} catch (IllegalArgumentException e) {
				// Name was not all lower-case or adjusted name is not found
				return null;
			}
		}

		public String getReturns() {
			NonObjectReturnType t = getNonObjectReturnType();
			return (t == null) ? returns : t.typeName();
		}

		public void setReturns(String returns) {
			if (returns.startsWith("<") && returns.endsWith(">")) {
				returns = returns.substring(1, returns.length() - 1);
				setReturnsGeneric(true);
			}
			this.returns = returns;
		}

		public boolean getReturnsVoid() {
			NonObjectReturnType t = getNonObjectReturnType();
			return (t != null) && t.isVoid();
		}

		public boolean getReturnsPrimitive() {
			NonObjectReturnType t = getNonObjectReturnType();
			return (t != null) && !t.isVoid();
		}

		public boolean getReturnsObject() {
			NonObjectReturnType t = getNonObjectReturnType();
			return (t == null);
		}

		public boolean getReturnsValue() {
			return !getReturnsVoid();
		}

		private static String defaultValueForType(String returnTypeName) {
			NonObjectReturnType t = getNonObjectReturnType(returnTypeName);

			if (t == null) {
				return "null";
			}
			return t.getDefaultValueExpression();
		}

		public String[] getThrows() {
			return throws_;
		}

		public EnglishListItem[] getThrowsInEnglish() {
			return EnglishListItem.createListItems(throws_, "or");
		}

		public void setThrows(String[] throws_) {
			log.info("throws being set to: " + Arrays.toString(throws_));
			this.throws_ = throws_;
		}

		public String getDefault() {
			if (default_ == null) {
				return defaultValueForType(returns);
			}
			return default_;
		}

		public void setDefault(String default_) {
			this.default_ = default_;
		}

		public boolean getReturnsGeneric() {
			return getReturnsObject() && returnsGeneric;
		}

		public void setReturnsGeneric(boolean returnsGeneric) {
			this.returnsGeneric = returnsGeneric;
		}

	}
}
