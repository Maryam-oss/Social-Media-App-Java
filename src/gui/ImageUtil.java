package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class ImageUtil {

    public static ImageIcon loadImageFromPath(String path, int width, int height) {
        try {
            if (path == null || path.isEmpty()) {
                return createPlaceholder(width, height);
            }

            Image img = null;
            if (path.startsWith("http")) {
                img = new ImageIcon(new URL(path)).getImage();
            } else if (new File(path).exists()) {
                img = new ImageIcon(path).getImage();
            } else {
                // Try to load from GridFS ONLY if it looks like a valid ObjectId
                if (isValidObjectId(path)) {
                    try {
                        com.mongodb.client.gridfs.GridFSBucket gridFSBucket = dao.DBConnection.getGridFSBucket();
                        org.bson.types.ObjectId fileId = new org.bson.types.ObjectId(path);
                        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
                        gridFSBucket.downloadToStream(fileId, outputStream);
                        byte[] imageBytes = outputStream.toByteArray();
                        img = new ImageIcon(imageBytes).getImage();
                    } catch (Exception e) {
                        System.out.println("ImageUtil: Failed to load from GridFS: " + e.getMessage());
                        return createPlaceholder(width, height);
                    }
                } else {
                    // Not a file and not a valid ObjectId -> Broken link
                    System.out.println("ImageUtil: Invalid path or ObjectId: " + path);
                    return createPlaceholder(width, height);
                }
            }

            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);

        } catch (Exception e) {
            System.out.println("ImageUtil: Exception loading image: " + e.getMessage());
            e.printStackTrace();
            return createPlaceholder(width, height);
        }
    }

    private static boolean isValidObjectId(String s) {
        if (s == null)
            return false;
        int len = s.length();
        if (len != 24)
            return false;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static ImageIcon createPlaceholder(int width, int height) {
        return createPlaceholder(width, height, "No Image");
    }

    public static ImageIcon createPlaceholder(int width, int height, String text) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        g2d.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon createVideoPlaceholder(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        // Background
        g2d.setColor(new Color(40, 40, 40)); // Dark background for video
        g2d.fillRect(0, 0, width, height);

        // Play Icon (Simple Triangle)
        g2d.setColor(Color.WHITE);
        int[] xPoints = { width / 2 - 20, width / 2 - 20, width / 2 + 30 };
        int[] yPoints = { height / 2 - 30, height / 2 + 30, height / 2 };
        g2d.fillPolygon(xPoints, yPoints, 3);

        // Text
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String text = "Video";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = height / 2 + 60;
        g2d.drawString(text, x, y);

        g2d.dispose();
        return new ImageIcon(img);
    }
}
