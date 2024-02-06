package mealplanner;

import java.util.*;

public class Main {
  private final Scanner scanner = new Scanner(System.in);
  private final DatabaseConnection dbConnection = DatabaseConnection.createDatabaseConnection();
  private final List<Recipe> recipes = new ArrayList<>();

  private final ShoppingListManager shoppingListManager = new ShoppingListManager();
  private final String INVALID_CATEGORY = "Wrong meal category! Choose from: breakfast, lunch, dinner.";
  private final String INVALID_MEAL = "Wrong format. Use letters only!";

  public static void main(String[] args) {
    Main mealPlanner = new Main();
    mealPlanner.menu();
  }

  public void addMeal() {
    String mealCategory;
    println("Which meal do you want to add (breakfast, lunch, dinner)?");
    while (true) {
      mealCategory = scanner.nextLine();
      if (!isValidMealCategory(mealCategory)) {
        println(INVALID_CATEGORY);
      } else {
        break;
      }
    }

    String mealName;
    println("Input the meal's name:");
    while (true) {
      mealName = scanner.nextLine();
      if (!isValidMealName(mealName)) {
        println(INVALID_MEAL);
      } else {
        break;
      }
    }

    String ingredientInput;
    println("Input the ingredients:");
    while (true) {
      ingredientInput = scanner.nextLine();
      if (!isValidIngredient(ingredientInput)) {
        println(INVALID_MEAL);
      } else {
        break;
      }
    }

    String[] ingredients = ingredientInput.split(",");
    List<String> ingredientList = Arrays.asList(ingredients); // Convert String[] to List<String>

    int mealId = DatabaseConnection.generateRandomId();
    Recipe recipe = new Recipe(Category.valueOf(mealCategory.toUpperCase()), mealName, ingredientList, mealId); // Pass ingredientList instead of ingredients

    dbConnection.saveMeal(recipe);
    dbConnection.saveIngredients(recipe);

    recipes.add(recipe);

    println("The meal has been added!");
  }

  public void showMeals() {
    while (true) {
      println("Which category do you want to print (breakfast, lunch, dinner)?");
      String categoryInput = scanner.nextLine().toLowerCase();

      if (!isValidMealCategory(categoryInput)) {
        println(INVALID_CATEGORY);
      } else {
        boolean found = false;

        for (Recipe recipe : recipes) {
          if (recipe.getMealCategory().toString().toLowerCase().equals(categoryInput)) {
            if (!found) {
              println("Category: " + recipe.getMealCategory().toString().toLowerCase());
              System.out.println();
              found = true;
            }
            recipe.printRecipe();
          }
        }

        if (!found) {
          println("No meals found.");
        }
        break;
      }
    }
  }

  public void planMeals() {
    for (DayOfWeek day : DayOfWeek.values()) {
      println(day.getLabel());
      for (Category category : Category.values()) {
        List<String> mealNames = getMealNames(category);
        printMealNames(mealNames);
        println("Choose the " + category.toString().toLowerCase() + " for " + day.getLabel() + " from the list above:");
        String chosenMeal = scanner.nextLine().trim().toLowerCase();

        if (!mealNames.contains(chosenMeal)) {
          println("This meal doesn’t exist. Choose a meal from the list above.");
          chosenMeal = scanner.nextLine().trim().toLowerCase();
        }
        dbConnection.savePlan(day.getLabel(), findRecipe(chosenMeal));
      }
      println("Yeah! We planned the meals for " + day.getLabel() + ".");
      System.out.println();;
    }
    printPlan();
  }

  public void printPlan() {
    for (DayOfWeek day : DayOfWeek.values()) {
      println(day.getLabel());
      for (Category category : Category.values()) {
        println(category.toString() + ": " + dbConnection.getPlannedMeals(day.getLabel(), category));
      }
      System.out.println();;
    }
  }

  public List<String> getMealNames(Category category) {
    List<String> mealNames = new ArrayList<>();
    for (Recipe recipe : recipes) {
      if (recipe.getMealCategory() == category) {
        mealNames.add(recipe.getMealName().toLowerCase());
      }
    }
    Collections.sort(mealNames);
    return mealNames;
  }

  public Recipe findRecipe(String mealName) {
    for (Recipe recipe : recipes) {
      if (recipe.getMealName().equalsIgnoreCase(mealName)) {
        return recipe;
      }
    }
    return null;
  }

  public void printMealNames(List<String> mealNames) {
    for (String meal : mealNames) {
      println(meal);
    }
  }

  public boolean isValidMealCategory(String input) {
    return input.matches("breakfast|lunch|dinner");
  }

  public boolean isValidMealName(String input) {
    return input.matches("^\\s*[a-zA-Z]+(?:\\s+[a-zA-Z]+)*\\s*$");
  }

  public boolean isValidIngredient(String input) {
    return input.matches("^\\s*\\b[a-zA-Z]+(?:\\s+[a-zA-Z]+)*\\s*(?:,\\s*\\b[a-zA-Z]+(?:\\s+[a-zA-Z]+)*\\s*)*$");
  }

  public void println(String text) {
    System.out.println(text);
  }

  private void saveShoppingList() {
    if (shoppingListManager.isPlanReady()) {
      println("Input a filename:");
      String filename = scanner.nextLine();
      if (shoppingListManager.generateAndSaveShoppingList(filename)) {
        println("Saved!");
      } else {
        println("Error saving the shopping list.");
      }
    } else {
      println("Unable to save. Plan your meals first.");
    }
  }

  public void menu() {
    dbConnection.createPlanTable();
    recipes.addAll(dbConnection.getAllMeals());
    while (true) {
      println("What would you like to do (add, show, plan, save, exit)?");
      String option = scanner.nextLine();

      switch (option) {
        case "add":
          addMeal();
          break;
        case "show":
          showMeals();
          break;
        case "plan":
          planMeals();
          break;
        case "save":
          saveShoppingList();
          break;
        case "exit":
          println("Bye!");
          System.exit(0);
        default:
          if (!isValidMealCategory(option)) {
            println("Invalid option. Choose again!");
          }
      }
    }
  }
}