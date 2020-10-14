package user;

public class Dice {
    private String color;
    private int rehearseCredits;

    public Dice(String color) {
        this.color = color;
        this.rehearseCredits = 0;
    }

    public String getColor() {
        return color;
    }

    public int getRehearseCredits() {
        return rehearseCredits;
    }

    public void incrementRehearseCredits() {
        this.rehearseCredits++;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean sceneRoll(int budget) {
        int rollVal = ((int)(Math.random() * 6) + 1);
        System.out.println("A "+rollVal+" is rolled.");
        if(rollVal >= budget){
            return true;
        } else {
            return false;
        }
    }

    public int roll() {
        int rollVal = ((int)(Math.random() * 6) + 1);

        return rollVal;
    }

}
