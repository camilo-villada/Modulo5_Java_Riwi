package com.corporatetalenthub.service;

import com.corporatetalenthub.model.Developer;
import com.corporatetalenthub.model.Manager;
import com.corporatetalenthub.model.Person;
import com.corporatetalenthub.model.ProfileValidationResult;

public class ProfileValidator {

    // Legacy Java 8/11 style: instanceof followed by mandatory manual casting.
    public ProfileValidationResult validateLegacy(Person person) {
        if (person instanceof Developer) {
            String language = ((Developer) person).getPrimaryLanguage();
            return new ProfileValidationResult(ProfileValidationResult.Type.DEVELOPER, language);
        }
        if (person instanceof Manager) {
            double budget = ((Manager) person).getMonthlyBudget();
            return new ProfileValidationResult(ProfileValidationResult.Type.MANAGER, String.valueOf(budget));
        }
        return new ProfileValidationResult(ProfileValidationResult.Type.GENERAL, "");
    }

    // Java 17/21 style: pattern matching removes manual cast repetition.
    public ProfileValidationResult validateModern(Person person) {
        if (person instanceof Developer developer) {
            return new ProfileValidationResult(ProfileValidationResult.Type.DEVELOPER, developer.getPrimaryLanguage());
        }
        if (person instanceof Manager manager) {
            return new ProfileValidationResult(ProfileValidationResult.Type.MANAGER, String.valueOf(manager.getMonthlyBudget()));
        }
        return new ProfileValidationResult(ProfileValidationResult.Type.GENERAL, "");
    }
}
