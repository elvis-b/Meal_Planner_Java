package mealplanner;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

public class ShoppingListManager {

    private final String DB_URL = "jdbc:postgresql://localhost/meals_db";
    private final String USER = "postgres";
    private final String PASS = "1111";

    public boolean isPlanReady() {
        for (DayOfWeek day : DayOfWeek.values()) {
            if (!isDayPlanned(day.getLabel())) {
                return false;
            }
        }
        return true;
    }

    public boolean isDayPlanned(String dayOfWeek) {
        return !getPlannedMeals(dayOfWeek).isEmpty();
    }

    public boolean generateAndSaveShoppingList(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Map to store ingredients and their counts
            Map<String, Integer> ingredientCounts = new HashMap<>();

            for (DayOfWeek day : DayOfWeek.values()) {
                List<Recipe> dayMeals = getPlannedMeals(day.getLabel());
                for (Recipe meal : dayMeals) {
                    for (String ingredient : meal.getIngredients()) {
                        ingredientCounts.put(ingredient, ingredientCounts.getOrDefault(ingredient, 0) + 1);
                    }
                }
            }

            // Write ingredients and their counts to the file
            for (Map.Entry<String, Integer> entry : ingredientCounts.entrySet()) {
                String ingredient = entry.getKey();
                int count = entry.getValue();
                if (count > 1) {
                    writer.println(ingredient + " x" + count);
                } else {
                    writer.println(ingredient);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Recipe> getPlannedMeals(String dayOfWeek) {
        List<Recipe> plannedMeals = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM plan WHERE day_of_week=?")) {

            statement.setString(1, dayOfWeek);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String category = resultSet.getString("category");
                String mealName = resultSet.getString("meal");
                plannedMeals.add(findRecipe(mealName, category));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving planned meals", e);
        }
        return plannedMeals;
    }

    public Recipe findRecipe(String mealName, String category) {
        List<Recipe> recipes = DatabaseConnection.createDatabaseConnection().getAllMeals();
        for (Recipe recipe : recipes) {
            if (recipe.getMealName().equalsIgnoreCase(mealName) && recipe.getMealCategory().toString().equalsIgnoreCase(category)) {
                return recipe;
            }
        }
        return null;
    }
}
