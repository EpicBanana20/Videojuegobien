package Niveles;

public class Level {
    private int[][] lvlData;

    public Level(int[][] lvlData) {
        this.lvlData = lvlData;
    }
    public int getSpriteIndex(int x, int y){
        return lvlData[x][y];
    }
    public int[][] getLvlData() {
        return lvlData;
    }
    public void setLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
    }
    
}
