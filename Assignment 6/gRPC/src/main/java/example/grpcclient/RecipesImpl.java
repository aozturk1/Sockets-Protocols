package example.grpcclient;

import io.grpc.stub.StreamObserver;
import service.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecipesImpl extends RecipeGrpc.RecipeImplBase implements Serializable{

    Map<Integer, RecipeEntry> recipes;
    Map<Integer, Integer> ratings;
    public static String file = "recipes.data";

    public RecipesImpl() {
        this.recipes = new HashMap<Integer, RecipeEntry>();
        this.ratings = new HashMap<Integer, Integer>();
    }

    @Override
    public void addRecipe(RecipeReq request, StreamObserver<RecipeResp> responseObserver) {
        String name = request.getName();
        String author = request.getAuthor();
        List<Ingredient> ingredients = request.getIngredientList();
        int id = recipes.size() + 1;

        RecipeResp.Builder builder = RecipeResp.newBuilder()
                .setIsSuccess(true);
        //book has title
        if (name != null) {
            RecipeEntry recipeEntry = RecipeEntry.newBuilder()
                    .setName(name)
                    .setAuthor(author)  //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    //.setAuthor(author + "_" + 0)
                    .addAllIngredient(ingredients)
                    .setRating(0)
                    .setId(id).build();
            recipes.put(id, recipeEntry);
            ratings.put(id, 0); //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            builder.setIsSuccess(true);
            builder.setMessage("Thank you for adding " + name + " to our collection!");
            //book has no title
        } else {
            builder.setError("Oops, There was a problem with adding the recipe to our collection!");
        }

        //Send the response back to the client
        RecipeResp response = builder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        if (!name.equals(null)) {
            save();
        }
    }

    @Override
    public void viewRecipes(com.google.protobuf.Empty request, StreamObserver<RecipeViewResp> responseObserver) {
        RecipeViewResp.Builder builder = RecipeViewResp.newBuilder()
                .setIsSuccess(true);
        if (recipes.isEmpty()) {
            builder.setIsSuccess(false);
            builder.setError("Oops, no recipes yet, try adding!");
        } else {
            builder.addAllRecipes(recipes.values());
        }
        RecipeViewResp response = builder.build();

        //Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void rateRecipe(RecipeRateReq request, StreamObserver<RecipeResp> responseObserver) {
        int id = request.getId();
        System.out.println(id + "line79");
        float rating = request.getRating();
        System.out.println(rating+ "line81");

//        String ratingStore = oldRecipeEntry.getAuthor();  //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//        String[] parts = ratingStore.split("_");
//        String author = parts[0];
//        int rateCount = Integer.parseInt(parts[1]);
//        rateCount++;
//        float newRating = rating / rateCount;
//        RecipeEntry newRecipeEntry = RecipeEntry.newBuilder()
//                .setName(oldRecipeEntry.getName())
//                .setAuthor(author + "_" + rateCount)
//                .addAllIngredient(oldRecipeEntry.getIngredientList())
//                .setRating(newRating)
//                .setId(id).build();
//        recipes.replace(id,oldRecipeEntry, newRecipeEntry);  //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        RecipeResp.Builder builder = RecipeResp.newBuilder()
                .setIsSuccess(true);
        if (recipes.isEmpty()) {
            builder.setIsSuccess(false);
            builder.setError("Oops, no recipes yet, try adding!");
            //book has no title
        } else {
            if (recipes.containsKey(id)) {
                //change recipeEntry rating
                RecipeEntry oldRecipeEntry = recipes.get(id); //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                System.out.println(oldRecipeEntry+ "line107");
                int oldRateCount = ratings.get(id);
                System.out.println(oldRateCount+ "line109");
                int newRateCount = oldRateCount+1;
                System.out.println(newRateCount+ "line111");
                float oldRating = oldRecipeEntry.getRating();
                System.out.println(oldRating+ "line113");
                float newRating = ((oldRating * oldRateCount) + rating) / newRateCount;
                System.out.println(newRating+ "line115");
                if (oldRateCount == 0){
                    newRating = rating;
                    System.out.println(newRating+ "line118");
                }
                System.out.println(id+ "line120");
                System.out.println(oldRateCount+ "line121");
                System.out.println(newRateCount+ "line122");
                ratings.replace(id, oldRateCount, newRateCount);
                System.out.println(newRating+ "line124");

                RecipeEntry newRecipeEntry = RecipeEntry.newBuilder()
                        .setName(oldRecipeEntry.getName())
                        .setAuthor(oldRecipeEntry.getAuthor())
                        .addAllIngredient(oldRecipeEntry.getIngredientList())
                        .setRating(newRating)
                        .setId(id).build();
                recipes.replace(id, oldRecipeEntry, newRecipeEntry);

                builder.setMessage("Thank you for rating " + id + "!");
            } else {
                builder.setIsSuccess(false);
                builder.setError("Oops, No such recipe!");
            }
        }
        RecipeResp response = builder.build();

        System.out.println(response+ "line141");
        //Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        if (!recipes.isEmpty()) {
            save();
        }
    }

    public static RecipesImpl load() {
        RecipesImpl loadRecipesImpl;
        try(FileInputStream in = new FileInputStream(file); ObjectInputStream os = new ObjectInputStream(in)) {
            loadRecipesImpl = (RecipesImpl) os.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new RecipesImpl();
        } return loadRecipesImpl;
    }

    private void save() {
        try (FileOutputStream out = new FileOutputStream(file); ObjectOutputStream os = new ObjectOutputStream(out)) {
            os.writeObject(this);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
