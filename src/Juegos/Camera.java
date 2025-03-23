package Juegos;

public class Camera {
    private int xLvlOffset, yLvlOffset;
    private int maxLvlOffsetX, maxLvlOffsetY;
    private float targetX, targetY;
    private float lerpFactor = 0.02f;

    public Camera(int gameWidth, int gameHeight, int levelWidth, int levelHeight) {
        this.maxLvlOffsetX = levelWidth - gameWidth;
        this.maxLvlOffsetY = levelHeight - gameHeight;
        this.xLvlOffset = 0;
        this.yLvlOffset = 0;

    }

    public void checkCloseToBorder(int playerX, int playerY) {
        targetX = playerX - Juego.GAME_WIDTH/2;
        targetY = playerY - Juego.GAME_HEIGHT/2;
        
        xLvlOffset += (targetX - xLvlOffset) * lerpFactor;
        yLvlOffset += (targetY - yLvlOffset) * lerpFactor;
        
        // Aplicar límites
        xLvlOffset = Math.max(0, Math.min(maxLvlOffsetX, (int)xLvlOffset));
        yLvlOffset = Math.max(0, Math.min(maxLvlOffsetY, (int)yLvlOffset));
    }

    public int getxLvlOffset() {
        return xLvlOffset;
    }

    public int getyLvlOffset() {
        return yLvlOffset;
    }
}
