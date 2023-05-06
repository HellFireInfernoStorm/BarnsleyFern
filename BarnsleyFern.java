import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BarnsleyFern {
    double x, y;
    
    final int w, h;
    BufferedImage img;

    double tA, tB, tC, tD;

    BarnsleyFern(int w, int h, int xOffset, int yOffset) {
        this.w = w;
        this.h = h;

        tA = (w - 2 * xOffset) / 9.9983;
        tB = xOffset;
        tC = -1 * (h - 2 * yOffset) / (2.1820 + 2.6558);
        tD = yOffset + (h - 2 * yOffset) * 2.6558 / (2.6558 + 2.1820);

        x = 0;
        y = 0;
    }

    private void f1() {
        x = 0;
        y = 0.16 * y;
    }

    private void f2() {
        double oldX = x;
        x = 0.85 * x + 0.04 * y;
        y = -0.04 * oldX + 0.85 * y + 1.6;
    }

    private void f3() {
        double oldX = x;
        x = 0.2 * x - 0.26 * y;
        y = 0.23 * oldX + 0.22 * y + 1.6;
    }

    private void f4() {
        double oldX = x;
        x = -0.15 * x + 0.28 * y;
        y = 0.26 * oldX + 0.24 * y + 0.44;
    }

    private void nextPoint() {
        double rand = Math.random();
        if (rand < 0.01) {
            f1();
        } else if (rand < 0.86) {
            f2();
        } else if (rand < 0.93) {
            f3();
        } else {
            f4();
        }
    }

    private void drawPoint() {
        int pX = (int) Math.round(tA * y + tB);
        int pY = (int) Math.round(tC * x + tD);
        // set c = -1 for white points
        int c = Color.HSBtoRGB((float) y / 10, 1f, 0.6f);
        img.setRGB(pX, pY, c);
    }
    
    public void generateImage(long nPoints, boolean transparentBG, String pathName) {
        try {
            initializeImg(transparentBG);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.out.println("\n\nHeap Memory may have run out due to the Image resolution being too large.");
            System.out.println("\nDecrease resolution or increase heap memory using the '-Xmx' JVM flag.");
            System.out.println("Specify new maximum heap memory in the format: -Xmx{amount}{unit}");
            System.out.println("\tunit : g-GB, m-MB");
            System.out.println("\nexample : java -Xmx8g BarnsleyFern");
            System.out.println("\nTry at your own risk.");
            System.exit(-1);
        }

        drawPoints(nPoints);
        saveImg(pathName);
    }

    private void initializeImg(boolean transparentBG) {
        System.out.println("Creating Image.\n");
        if (transparentBG) {
            img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        } else {
            img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        }
    }

    private void drawPoints(long nPoints) {
        System.out.println("Begin Drawing Points.");
        for (long i = 0; i < nPoints; i++) {
            drawPoint();
            nextPoint();
            // if (i % 1000000000 == 0) System.out.println(i);
        }
        System.out.println("Drawing Points Completed.\n");
    }

    private void saveImg(String pathName) {
        System.out.println("Creating File.");
        File file = new File(pathName);
        System.out.println("Saving Image to File.");
        try {
            ImageIO.write(img, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nFinished.");
    }

    public static void main(String[] args) {
        int width = 3840;
        int height = 2160;
        int xOffset = 100;
        int yOffset = 50;

        long nPoints = 10000000l;
        String pathStr = String.format("BarnsleyFern(%dx%d).png", width, height);
        
        boolean transparentBg = false;

        BarnsleyFern fern = new BarnsleyFern(width, height, xOffset, yOffset);
        fern.generateImage(nPoints, transparentBg, pathStr);
    }
}