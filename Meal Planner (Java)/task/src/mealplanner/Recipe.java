package mealplanner;

import java.util.Arrays;
import java.util.List;

public class Recipe {
    private final Category mealCategory;
    private final String mealName;
    private final List<String> ingredients;
    private final int mealId;

    public Recipe(Category mealCategory, String mealName, List<String> ingredients, int mealId) {
        this.mealCategory = mealCategory;
        this.mealName = mealName;
        this.ingredients = ingredients;
        this.mealId = mealId;
    }

    public Category getMealCategory() {
        return mealCategory;
    }

    public String getMealName() {
        return mealName;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public int getMealId() {
        return mealId;
    }

    public void printRecipe() {
        System.out.println("Name: " + mealName);
        System.out.println("Ingredients:");
        for (String ingredient : ingredients) {
            System.out.println(ingredient);
        }
        System.out.println();
    }
}
