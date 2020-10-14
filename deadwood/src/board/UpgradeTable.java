package board;

import java.util.HashMap;

public class UpgradeTable {
    private HashMap<Integer, int[]> table;

    public UpgradeTable() {
        HashMap<Integer, int[]> map = new HashMap<Integer,int[]>();
        int[] addition = {4,5};
        map.put(2,addition);
        int[] addition2 = {10,10};
        map.put(3,addition2);
        int[] addition3 = {18,15};
        map.put(4,addition3);
        int[] addition4 = {28,20};
        map.put(5,addition4);
        int[] addition5 = {40,25};
        map.put(6,addition5);
        table = map;
    }

    public HashMap<Integer, int[]> getTable() {
        return table;
    }

    public void setTable(HashMap<Integer, int[]> table) {
        this.table = table;
    }

    /*
    public int getByDollars(int cost){
        return 1;
    }
    public int getByCredits(int cost){
        return 1;
    }
    public int getByRank(int rank){
        return 1;
    }*/
}