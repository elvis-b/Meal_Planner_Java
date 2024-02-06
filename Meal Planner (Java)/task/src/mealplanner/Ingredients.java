package mealplanner;

import java.util.ArrayList;
import java.util.List;

public class Ingredients {
    private final List<String> ingredientsList;

    public Ingredients(String[] ingredients) {
        this.ingredientsList = new ArrayList<>();
        for (String ingredient: ingredients) {
            this.ingredientsList.add(ingredient.trim());
        }
    }

    public void printIngredients() {
        for (String ingredient: ingredientsList) {
            System.out.println(ingredient);
        }
    }
}
