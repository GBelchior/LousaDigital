/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lousadigital.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lousadigital.camera.processors.CameraCursor;

/**
 *
 * @author gabri
 */
public class ColorPicker extends DigitalBoardOverlay
{
    private final Color[] colorPallete;
    private final Color transparent;

    private final int numColumns;
    private final int numRows;

    private final int rectWidth;
    private final int rectHeight;

    private Image cursorImage;
    private Image cursorClickedImage;
    private Image eraserImage;

    private int curColumn;
    private int curRow;

    public ColorPicker(Rectangle rect)
    {
        super(rect);

        transparent = new Color(255, 122, 255, 0);

        colorPallete = new Color[]
        {
            transparent,
            Color.BLACK,
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.CYAN,
            Color.MAGENTA,
            Color.YELLOW
        };

        curRow = 1;
        
        numColumns = 1;
        numRows = colorPallete.length / numColumns;

        rectWidth = (int) (rect.width / (double) numColumns);
        rectHeight = (int) (rect.height / (double) numRows);

        try
        {
            cursorImage = ImageIO.read(new File("./images/cursor.png"));
            cursorClickedImage = ImageIO.read(new File("./images/cursor-clicked.png"));
            eraserImage = ImageIO.read(new File("./images/eraser.png"));
        }
        catch (IOException ex)
        {
            Logger.getLogger(ColorPicker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void drawOverlay(Graphics2D destGraphics, CameraCursor cursor)
    {
        for (int i = 0; i < numRows; i++)
        {
            for (int j = 0; j < numColumns; j++)
            {
                int desloc = (curRow == i && curColumn == j) ? 10 : 0;
                
                Color curColor = colorPallete[i * numColumns + j];
                int curX = rect.x + rectWidth * j + desloc;
                int curY = rect.y + rectHeight * i;


                if (curColor == transparent)
                {
                    destGraphics.setColor(Color.GRAY);
                    destGraphics.fillRoundRect(curX, curY, rectWidth, rectHeight, 30, 30);

                    destGraphics.drawImage(eraserImage, curX + 5, curY + 5, rectWidth - 10, rectHeight - 10, null);
                }
                else
                {
                    destGraphics.setColor(curColor);
                    destGraphics.fillRoundRect(curX, curY, rectWidth, rectHeight, 30, 30);
                }

            }
        }

        if (isInColorPickerArea(cursor))
        {
            Image cursorToDraw = cursorImage;
            if (cursor.isClicked())
            {
                cursorToDraw = cursorClickedImage;
            }

            destGraphics.drawImage(
                    cursorToDraw,
                    (int) (cursor.getX() - cursor.getWidth() / 2f),
                    (int) (cursor.getY() - cursor.getHeight() / 2f),
                    null
            );
        }

        setCurrentColor(cursor);
    }

    private void setCurrentColor(CameraCursor cursor)
    {
        if (!isInColorPickerArea(cursor) || !cursor.isClicked())
            return;

        curColumn = (int) Math.ceil(((cursor.getRelativeX(rect) / (double) (numColumns * rectWidth))) * numColumns) - 1;
        curRow = (int) Math.ceil(((cursor.getRelativeY(rect) / (double) (numRows * rectHeight))) * numRows) - 1;

        cursor.setColor(colorPallete[curRow * numColumns + curColumn]);
    }

    private boolean isInColorPickerArea(CameraCursor cursor)
    {
        return cursor.isInScreen() && cursor.isBoundedBy(rect);
    }
}
