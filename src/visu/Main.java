package visu;

import tools.Preference;

public class Main {

    public static void main(String[] args) {

        /*
         * try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (ClassNotFoundException e) { // TODO Auto-generated
         * catch block e.printStackTrace(); } catch (InstantiationException e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
         * (IllegalAccessException e) { // TODO Auto-generated catch block e.printStackTrace(); } catch (UnsupportedLookAndFeelException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); }
         */

        String toto = Preference.getPreference("pathLab");

        Ihm ihm = new Ihm();
        ihm.setVisible(true);

    }

}
