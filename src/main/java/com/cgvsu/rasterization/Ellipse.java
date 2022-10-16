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

        if (!ifFromUpperLeftCorner) {
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


        rasterizationBorder(pixelWriter, cCenter, cBorder, rx, ry, xc, yc);
        pixelWriter.setColor((int) xc, (int) yc, cCenter);
    }

    private void rasterizationBorder(PixelWriter pixelWriter, Color cCenter, Color cBorder, float rx, float ry,
                                     float xc, float yc) {
        double[] ds = findColorsDifference(cCenter, cBorder);
        float dx, dy, d1, d2, x, y;

        x = 0;
        y = ry;

        d1 = (ry * ry) - (rx * rx * ry) +
                (0.25f * rx * rx);
        dx = 2 * ry * ry * x;
        dy = 2 * rx * rx * y;

        while (dx < dy) {

            interpolation(pixelWriter, ds, cCenter, cBorder, xc, yc, x + xc, -x + xc, y + yc, rx, ry);
            interpolation(pixelWriter, ds, cCenter, cBorder, xc, yc, x + xc, -x + xc, -y + yc, rx, ry);
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
            interpolation(pixelWriter, ds, cCenter, cBorder, xc, yc, x + xc, -x + xc, -y + yc, rx, ry);
            interpolation(pixelWriter, ds, cCenter, cBorder, xc, yc, x + xc, -x + xc, y + yc, rx, ry);
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
        /*if (rx > 0 && ry > 0) {
        rasterizationBorder(pixelWriter, cCenter, cBorder, (float) (rx - 0.0375), (float) (ry - 0.0375),
        xc, yc);
        }*/
    }

    private void interpolation(PixelWriter pixelWriter, double[] ds, Color cCenter, Color cBorder, float xc, float yc,
                               float xb1, float xb2, float yb, float rx, float ry) {
        float s;
        Color tmp;
        int dr;
        int dg;
        int db;


        for (int i = 1; i < Math.abs(xb2 - xb1); i++) {
            s = (float) Math.sqrt((xc - (xb2 + i)) * (xc - (xb2 + i)) + (yc - yb) * (yc - yb));
            dr = ifRightColorRGB( (int) ((ds[0] * s) + cCenter.getRed() * 255));
            dg = ifRightColorRGB( (int) ((ds[1] * s) + cCenter.getGreen() * 255));
            db = ifRightColorRGB( (int) ((ds[2] * s) + cCenter.getBlue() * 255));
            tmp = Color.rgb(dr, dg, db);


            pixelWriter.setColor( (int) (xb2 + i), (int) yb, tmp);
        }
    }

    private int ifRightColorRGB(int rgb) {
        if (rgb < 0) {
            return 0;
        }
        if (rgb > 255) {
            rgb = 255;
        }
        return  rgb;
    }
    private double[] findColorsDifference(Color color1, Color color2) {
        double sr = (a / 2 + b / 2) / 2;
        double Dr = (color1.getRed() - color2.getRed()) * 255 / sr;
        double Dg = (color1.getGreen() - color2.getGreen()) * 255 / sr;
        double Db = (color1.getBlue() - color2.getBlue()) * 255 / sr;

        Dr = (color1.getRed() - color2.getRed() >= 0) ? Math.abs(Dr) * -1 : Math.abs(Dr);
        Dg = (color1.getGreen() - color2.getGreen() >= 0) ? Math.abs(Dg) * -1 : Math.abs(Dg);
        Db = (color1.getBlue() - color2.getBlue() >= 0) ? Math.abs(Db) * -1 : Math.abs(Db);

        return new double[] {Dr, Dg, Db};
    }
}
