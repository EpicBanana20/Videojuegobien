package Juegos;

public class GameLoop extends Thread {
    private Juego game;
    private int FPS_SET = 120;
    private int UPS_SET = 200;

    public GameLoop(Juego game) {
        this.game = game;
    }

    public void run() {
        double frameportiempo = 1000000000.0 / FPS_SET;
        double updateportiempo = 1000000000.0 / UPS_SET;
        //int frame = 0;
        //int update = 0;
        long previusTime = System.nanoTime();
        double deltaU = 0;
        double deltaF = 0;
        long lastCheck = System.currentTimeMillis();
        while (true) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - previusTime) / updateportiempo;
            deltaF += (currentTime - previusTime) / frameportiempo;
            previusTime = currentTime;
            if (deltaU >= 1) {
                
                // Realiza la actualización normal del juego
                game.updates();
                //update++;
                deltaU--;
            }
            if (deltaF >= 1) {
                game.getPanel().repaint();
                //frame++;
                deltaF--;
            }
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                //System.out.println("FPS:" + frame + "| UPS: " + update);
                //frame = 0;
                //update = 0;
            }
        }
    }
}
