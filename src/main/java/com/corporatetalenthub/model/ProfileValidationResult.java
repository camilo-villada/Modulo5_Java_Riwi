package com.corporatetalenthub.model;

public record ProfileValidationResult(Type type, String detail) {

	public enum Type {
		DEVELOPER,
		MANAGER,
		GENERAL
	}
}
