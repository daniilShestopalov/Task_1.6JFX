package com.cgvsu.rasterization;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;

public class Ellipse {

    final private float xMidPoint;
    final private float yMtdPoint;
    final private float a;//ширина
    final private float b;//высота

    final private GraphicsContext graphicsContext;



    public Ellipse(float x, float y, float a, float b, boolean ifFromUpperLeftCorner, GraphicsContext graphicsContext) {
        this.a = a;
        this.b = b;
        this.graphicsContext = graphicsContext;

        if (ifFromUpperLeftCorner) {
            this.xMidPoint = x;
            this.yMtdPoint = y;
        } else {
            float xMidPoint = x + a/2;
            float yMidPoint = y + b/2;
            this.xMidPoint = xMidPoint;
            this.yMtdPoint = yMidPoint;
        }
    }

    public float getX() {
        return xMidPoint;
    }

    public float getY() {
        return yMtdPoint;
    }

    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }

    public void rasterization(Color cCenter, Color cBorder) {
        float rx = getA()/2;
        float ry = getB()/2;
        float xc = getX();
        float yc = getY();

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        pixelWriter.setColor((int) rx, (int) ry, cCenter);

        float dx, dy, d1, d2, x, y;

        x = 0;
        y = ry;

        d1 = (ry * ry) - (rx * rx * ry) +
                (0.25f * rx * rx);
        dx = 2 * ry * ry * x;
        dy = 2 * rx * rx * y;

        while (dx < dy) {
            pixelWriter.setColor((int) (x + xc), (int) (y + yc), cBorder);
            pixelWriter.setColor((int) (-x + xc), (int) (y + yc), cBorder);
            pixelWriter.setColor((int) (x + xc), (int) (-y + yc), cBorder);
            pixelWriter.setColor((int) (-x + xc), (int) (-y + yc), cBorder);

            x++;
            if (d1 < 0) {
                dx = dx + (2 * ry * ry);
                d1 = d1 + dx + (ry * ry);
            } else {
                y--;
                dx = dx + (2 * ry * ry);
                dy = dy - (2 * rx * rx);
                d1 = d1 + dx - dy + (ry * ry);
            }
        }

        d2 = ((ry * ry) * ((x + 0.5f) * (x + 0.5f)))
                + ((rx * rx) * ((y - 1) * (y - 1)))
                - (rx * rx * ry * ry);
        while (y >= 0) {
            pixelWriter.setColor((int) (x + xc), (int) (y + yc), cBorder);
            pixelWriter.setColor((int) (-x + xc), (int) (y + yc), cBorder);
            pixelWriter.setColor((int) (x + xc), (int) (-y + yc), cBorder);
            pixelWriter.setColor((int) (-x + xc), (int) (-y + yc), cBorder);

            y--;
            if (d2 > 0) {
                dy = dy - (2 * rx * rx);
                d2 = d2 + (rx * rx) - dy;
            } else {
                x++;
                dx = dx + (2 * ry * ry);
                dy = dy - (2 * rx * rx);
                d2 = d2 + dx - dy + (rx * rx);
            }
        }
    }
}
