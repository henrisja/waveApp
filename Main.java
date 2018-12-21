package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


class buttonPanel extends JPanel {
    public JButton generateNew;
    public JButton singleWave;
    public JButton continuousWave;

    public buttonPanel(){
        ActionListener listener = new buttonListener(this);
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        generateNew = new JButton("Generate New");
        generateNew.addActionListener(listener);
        this.add(generateNew);
        singleWave = new JButton("Single Wave");
        singleWave.addActionListener(listener);
        this.add(singleWave);
        continuousWave = new JButton("Continuous Wave");
        continuousWave.addActionListener(listener);
        this.add(continuousWave);
    }
}

class buttonListener implements ActionListener{

    private buttonPanel panel;

    public buttonListener(buttonPanel p){
        this.panel = p;
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == panel.generateNew){
            //CODE FOR NEW WAVE PATTERN
        }
        else if(source == panel.singleWave){
            //CODE FOR SINGLE WAVE PATTERN
        }
        else{
            //CODE FOR THE CONTINUOUS WAVE
        }
    }
}

class wavyFrame extends JFrame
{
    public wavyFrame()
    {
        setTitle("TESTING_WAVES");
        setBounds(0, 0, 1024, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class linePanel extends JPanel{

    private ArrayListMatrix newMatrix;

    public linePanel(){

        newMatrix = new ArrayListMatrix();
        newMatrix.generateFirstThreeRows();
        newMatrix.createNewRows();
    }

    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        newMatrix.drawLines(g2d);
    }
}

public class Main {

    public static void main(String[] args) {

        linePanel lines = new linePanel();
        JFrame frame = new wavyFrame();
        frame.add(lines);
        frame.setVisible(true);

    }
}
