/*
 * Creation : 30 nov. 2017
 */
package theme;

import java.awt.Color;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public final class DarkTheme extends DefaultMetalTheme {

    @Override
    protected ColorUIResource getPrimary1() {
        // TODO Auto-generated method stub
        return new ColorUIResource(Color.BLACK);
    }

    @Override
    protected ColorUIResource getPrimary2() {
        // Couleur progressbar
        return new ColorUIResource(Color.GRAY);
    }

    @Override
    protected ColorUIResource getPrimary3() {
        // Couleur barre de scroll
        return new ColorUIResource(Color.LIGHT_GRAY);
    }

    @Override
    protected ColorUIResource getSecondary1() {
        // Couleur de contour
        return new ColorUIResource(Color.DARK_GRAY);
    }

    @Override
    protected ColorUIResource getSecondary2() {
        // Contour type JTable
        return new ColorUIResource(Color.BLACK);
    }

    @Override
    protected ColorUIResource getSecondary3() {
        // Couleur de background
        return new ColorUIResource(Color.BLACK);
    }

    @Override
    public ColorUIResource getControlTextColor() {
        // TODO Auto-generated method stub
        return new ColorUIResource(Color.LIGHT_GRAY);
    }

    @Override
    public ColorUIResource getTextHighlightColor() {
        // TODO Auto-generated method stub
        return new ColorUIResource(Color.BLACK);
    }

    @Override
    public ColorUIResource getMenuForeground() {
        // TODO Auto-generated method stub
        return new ColorUIResource(Color.WHITE);
    }

    @Override
    public ColorUIResource getControlDarkShadow() {
        // TODO Auto-generated method stub
        return super.getControlDarkShadow();
    }

    @Override
    public ColorUIResource getControlShadow() {
        // TODO Auto-generated method stub
        return new ColorUIResource(Color.DARK_GRAY);
    }

    @Override
    public ColorUIResource getControl() {
        // Non utile
        return super.getControl();
    }

    @Override
    public ColorUIResource getUserTextColor() {
        // TODO Auto-generated method stub
        return new ColorUIResource(Color.BLACK);
    }

    @Override
    public ColorUIResource getSystemTextColor() {
        // Couleur text JLabel
        return new ColorUIResource(Color.LIGHT_GRAY);
    }

}
