package com.corporatetalenthub.model;

public record RegistrationValidationResult(Status status, RegistrationResult registration) {

	public enum Status {
		SUCCESS,
		INVALID_NAME,
		INVALID_AGE,
		INVALID_SALARY
	}
}
