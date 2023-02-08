package com.cst2335.cocktailapp;

import android.graphics.Bitmap;

/**
 * this class to create Cocktail Object that contains id,name,image,instructions,category,
 * and url of the cocktail
 */
public class Cocktail {
    long id;
    long cocktailId;
    Bitmap image;
    byte imageByte;
    String image_url;
    String name;
    String category;
    String instructions;
    String URL;

    /**
     * Will be used to add cocktail
     * @param id database id of the cocktail
     * @param cocktailId id of the cocktail
     * @param name name of the cocktail
     * @param image image of the cocktail
     * @param category category of the cocktail
     * @param instructions instructions of the cocktail
     * @param URL url of the cocktail
     */
    public Cocktail(long id,
                    long cocktailId,
                    String name,
                    Bitmap image,
                    String category,
                    String instructions,
                    String URL) {
        this.cocktailId = cocktailId;
        this.id = id;
        this.name = name;
        this.image = image;
        this.category = category;
        this.instructions = instructions;
        this.URL = URL;
    }

    /**
     * Will be used to add cocktail
     * @param cocktailId id of the cocktail
     * @param name name of the cocktail
     * @param image image of the cocktail
     * @param category category of the cocktail
     * @param instructions instructions of the cocktail
     * @param URL url of the cocktail
     */
    public Cocktail(long cocktailId,
                    String name,
                    Bitmap image,
                    String category,
                    String instructions,
                    String URL) {
        this.cocktailId = cocktailId;
        this.name = name;
        this.image = image;
        this.category = category;
        this.instructions = instructions;
        this.URL = URL;
    }

    /**
     * will return database ID of the cocktail
     * @return database ID of the cocktail
     */
    public long getId() {
        return id;
    }

    /**
     * will return ID of the cocktail
     * @return ID of the cocktail
     */
    public long getCocktailId() {
        return cocktailId;
    }

    /**
     * will return Image of the cocktail
     * @return Image of the cocktail
     */
    public Bitmap getImage() {
        return image;
    }
    /**
     * will return ImageUrl of the cocktail
     * @return ImageUrl of the cocktail
     */
    public String getImageUrl(){return image_url;}
    /**
     * will return Name of the cocktail
     * @return Name of the cocktail
     */
    public String getName() { return name; }
    /**
     * will return Category of the cocktail
     * @return Category of the cocktail
     */
    public String getCategory() {
        return category;
    }
    /**
     * will return Instructions of the cocktail
     * @return Instructions of the cocktail
     */
    public String getInstructions() {
        return instructions;
    }
    /**
     * will return URL of the cocktail
     * @return URL of the cocktail
     */
    public String getURL(){return URL;}

}