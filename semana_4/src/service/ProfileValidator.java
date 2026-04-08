package service;

import model.Developer;
import model.Manager;
import model.Person;

public class ProfileValidator {

    // Legacy Java 8/11 style: instanceof followed by mandatory manual casting.
    public String validateLegacy(Person person) {
        if (person instanceof Developer) {
            String language = ((Developer) person).getPrimaryLanguage();
            return "Legacy -> Developer with primary language " + language;
        }
        if (person instanceof Manager) {
            double budget = ((Manager) person).getMonthlyBudget();
            return "Legacy -> Manager with monthly budget " + budget;
        }
        return "Legacy -> Profile without promotion rules";
    }

    // Java 17/21 style: pattern matching removes manual cast repetition.
    public String validateModern(Person person) {
        if (person instanceof Developer developer) {
            return "Modern -> Developer with primary language " + developer.getPrimaryLanguage();
        }
        if (person instanceof Manager manager) {
            return "Modern -> Manager with monthly budget " + manager.getMonthlyBudget();
        }
        return "Modern -> Profile without promotion rules";
    }
}
