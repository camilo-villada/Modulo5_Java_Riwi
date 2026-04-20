package contracts;

public interface Promotable {

    double calculatePromotionBonus(double performanceAverage);

    // Java 8+ default methods allow interfaces to evolve without breaking existing implementations.
    default void logOperation(String details) {
        System.out.println("[PROMOTION-LOG] " + details);
    }
}
