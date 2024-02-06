package mealplanner;

import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    private final String DB_URL = "jdbc:postgresql://localhost/meals_db";
    private final String USER = "postgres";
    private final String PASS = "1111";

    private static final int MIN_ID = 1;
    private static final int MAX_ID = 1000;

    public DatabaseConnection() {
        try {
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating tables", e);
        }
    }

    public static DatabaseConnection createDatabaseConnection() {
        return new DatabaseConnection();
    }

    public static int generateRandomId() {
        return new Random().nextInt(MAX_ID - MIN_ID + 1) + MIN_ID;
    }

    public void createTables() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = connection.createStatement()) {

            // Creating 'meals' table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals (\n" +
                    "  category VARCHAR(255),\n" +
                    "  meal VARCHAR(255),\n" +
                    "  meal_id INT\n" +
                    ")");

            // Creating 'ingredients' table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients (\n" +
                    "  ingredient VARCHAR(255),\n" +
                    "  ingredient_id INT,\n" +
                    "  meal_id INT\n" +
                    ")");
        }
    }

    public void saveMeal(Recipe meal) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO meals (category, meal, meal_id) VALUES (?, ?, ?)")) {

            statement.setString(1, meal.getMealCategory().toString());
            statement.setString(2, meal.getMealName());
            statement.setInt(3, meal.getMealId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveIngredients(Recipe meal) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO ingredients (ingredient, ingredient_id, meal_id) VALUES (?, ?, ?)")) {

            for (String ingredient : meal.getIngredients()) {
                statement.setString(1, ingredient.trim());
                statement.setInt(2, generateRandomId());
                statement.setInt(3, meal.getMealId());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Recipe> getAllMeals() {
        List<Recipe> meals;
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM meals JOIN ingredients ON meals.meal_id = ingredients.meal_id")) {

            Map<Integer, Recipe> recipeMap = new HashMap<>(); // Map to store recipes by meal_id

            while (resultSet.next()) {
                int mealId = resultSet.getInt("meal_id");

                if (!recipeMap.containsKey(mealId)) {
                    String category = resultSet.getString("category");
                    String mealName = resultSet.getString("meal");

                    Recipe recipe = new Recipe(Category.valueOf(category.toUpperCase()), mealName, new ArrayList<>(), mealId);
                    recipeMap.put(mealId, recipe);
                }

                String ingredient = resultSet.getString("ingredient");
                if (ingredient != null) {
                    recipeMap.get(mealId).getIngredients().add(ingredient);
                }
            }

            meals = new ArrayList<>(recipeMap.values());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return meals;
    }

    public void createPlanTable() {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plan (\n" +
                    "  day_of_week VARCHAR(255),\n" +
                    "  category VARCHAR(255),\n" +
                    "  meal VARCHAR(255),\n" +
                    "  meal_id INT\n" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating plan table", e);
        }
    }

    public void savePlan(String dayOfWeek, Recipe meal) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO plan (day_of_week, category, meal, meal_id) VALUES (?, ?, ?, ?)")) {

            statement.setString(1, dayOfWeek);
            statement.setString(2, meal.getMealCategory().toString());
            statement.setString(3, meal.getMealName());
            statement.setInt(4, meal.getMealId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving plan", e);
        }
    }

    public String getPlannedMeals(String dayOfWeek, Category category) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement("SELECT meal FROM plan WHERE day_of_week=? AND category=?")) {

            statement.setString(1, dayOfWeek);
            statement.setString(2, category.toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("meal");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving planned meal", e);
        }
        return "";
    }
}
