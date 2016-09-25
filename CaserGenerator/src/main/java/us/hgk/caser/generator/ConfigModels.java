package us.hgk.caser.generator;

import java.util.Arrays;

public class ConfigModels {
	public static class Union {
		private String packageName;
		private String name;
		private String doc;
		private String modifiers;
		private Case[] cases;

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

		@Override
		public String toString() {
			return "Union [packageName=" + packageName + ", name=" + name + ", doc=" + doc + ", modifiers=" + modifiers
					+ ", cases=" + Arrays.toString(cases) + "]";
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
}
