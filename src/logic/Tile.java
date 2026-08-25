package logic;

import java.util.ArrayList;

public class Tile {
    private ArrayList<Colour> promotionColours;
    private boolean water;

    public Tile(){
        Colour colour = Colour.Neutral;
       promotionColours = new ArrayList<Colour>();
        water = false;
    }

    public ArrayList<Colour> getPromotionColours() {
        return promotionColours;
    }

    public boolean getWater() {
        return water;
    }

    public void addPromotionColour(Colour colour) {
       promotionColours.add(colour);
    }


    public void setWater(boolean water) {
        this.water = water;
    }
}
