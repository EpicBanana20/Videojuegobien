package Elementos;

public class AimController {
    @SuppressWarnings("unused")
    private float mouseX, mouseY;
    private static float aimedX, aimedY; // posición “limitada” dentro del radio
    private float radius;
    private float angle;

    public AimController(float radius) {
        this.radius = radius;
    }

    public void update(float playerX, float playerY, float mouseX, float mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        // Calcular vector (dx, dy) desde el jugador hasta el mouse
        float dx = mouseX - playerX;
        float dy = mouseY - playerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        angle = (float) Math.atan2(dy, dx);
        // Si la distancia es mayor que el radio, la limitamos
        if (distance > radius) {
            float scale = radius / distance;
            dx *= scale;
            dy *= scale;
        }

        // Guardar la posición final “clampeada” al radio
        aimedX = playerX + dx;
        aimedY = playerY + dy;
    }

    public static float getAimedX() {
        return aimedX;
    }

    public static float getAimedY() {
        return aimedY;
    }

    public float getAngleRad() {
        return angle;
    }

    public float getAngleDeg() {
        return (float) Math.toDegrees(angle);
    }

    public static void getPositionAtDistance(float playerX, float playerY, float distance, float angle, float[] result) {
        result[0] = playerX + (float) Math.cos(angle) * distance;
        result[1] = playerY + (float) Math.sin(angle) * distance;
    }
}
