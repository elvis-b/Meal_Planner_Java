package mealplanner;

public enum DayOfWeek {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    private final String label;

    DayOfWeek(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
