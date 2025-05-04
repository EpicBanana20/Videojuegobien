package Elementos;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Elementos.Administradores.AdministradorBalas;

import java.awt.geom.AffineTransform;
import Utilz.LoadSave;

public abstract class Arma {
    protected BufferedImage sprite;
    protected float x, y;
    protected float offsetDistance; // Distancia desde el centro del jugador
    protected float rotacion; // En grados
    protected String nombre;
    protected float escala = 1.0f;
    protected boolean apuntadoIzquierda = false;
    protected String tipoDaño;
    protected AdministradorBalas adminBalas;

    private float[] positionResult = new float[2];

    public Arma(String spritePath, float offsetDistance, float escala, AdministradorBalas adminBalas) {
        this.sprite = LoadSave.GetSpriteAtlas(spritePath);
        this.offsetDistance = offsetDistance;
        this.escala = escala;
        this.adminBalas = adminBalas;
    }

    public void update(float playerX, float playerY, AimController aimController) {
        rotacion = aimController.getAngleRad();

        apuntadoIzquierda = (aimController.getAngleDeg() > 90 || aimController.getAngleDeg() < -90);
        AimController.getPositionAtDistance(
                playerX, playerY,
                offsetDistance,
                aimController.getAngleRad(),
                positionResult);

        x = positionResult[0];
        y = positionResult[1];
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {

        Graphics2D g2d = (Graphics2D) g;

        // IMPORTANTE: Guardar la transformación original
        AffineTransform originalTransform = g2d.getTransform();

        try {
            // Calcular el centro de rotación
            int centerX = (int) (x - xLvlOffset);
            int centerY = (int) (y - yLvlOffset);

            // En lugar de reemplazar, aplicamos una transformación adicional
            g2d.translate(centerX, centerY);
            g2d.rotate(rotacion);
            g2d.scale(escala, escala);
            if (apuntadoIzquierda) {
                g2d.scale(1, -1);
            }
            // Dibujar el sprite centrado
            int halfWidth = sprite.getWidth() / 2;
            int halfHeight = sprite.getHeight() / 2;
            g2d.drawImage(sprite, -halfWidth, -halfHeight, null);

        } finally {
            // CRÍTICO: Siempre restaurar la transformación original
            g2d.setTransform(originalTransform);
        }
    }

    // Métodos que se implementarán en clases derivadas
    public abstract void disparar();

    // GETTERS Y SETTERS
    public float getRotacion() {
        return rotacion;
    }

    public float getEscala() {
        return escala;
    }

    public String getNombre() {
        return nombre;
    }
    
    public AdministradorBalas getAdminBalas() {
        return adminBalas;
    }

    public String getTipoDaño() {
        return tipoDaño;
    }
}
