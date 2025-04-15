import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            createFolders();
        } catch (IOException ignored){}
        createScreen();

    }

    private static void createFolders() throws IOException {

    }

    public static void createScreen(){
        //gets the monitor resolution
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screen.getWidth();
        int height = (int) screen.getHeight();
        // Creates the main window and sets it's size and to the center of the screen
        JFrame mainWin = new JFrame("PictoNoise");
        mainWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWin.setSize((width / 2),( height / 2));
        mainWin.setLocationRelativeTo(null); // Somehow sets window to center of screen
        //Creates the panel that will hold all of the components and set it to the same size as the window
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS)); // Layout is set to box because I want to kill the little people in my magic code machine /s ??? 11022025 - What did I mean when I wrote this?
        mainPanel.setSize(mainWin.getWidth(),mainWin.getHeight());
        int panH = mainPanel.getHeight();
        int panW = mainPanel.getWidth();
        mainPanel.setBackground(Color.LIGHT_GRAY);
        JButton loadf = new JButton();
        JButton gen = new JButton();
        loadf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        gen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        mainPanel.add(loadf,gen);
        mainWin.setVisible(true);
    }
}