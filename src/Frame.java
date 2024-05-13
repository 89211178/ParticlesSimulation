import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int DEFAULT_NUM_PARTICLES = 200;
    private static final int DEFAULT_NUM_CYCLES = 100;

    private Panel panel;
    private JTextField widthField, heightField, particlesField, cyclesField;
    private JComboBox<String> modeComboBox;
    private JLabel elapsedTimeLabel;

    public Frame() {
        setTitle("Particle Simulation");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createControlPanel();
        panel = new Panel(this, false, DEFAULT_NUM_PARTICLES, DEFAULT_NUM_CYCLES);
        add(panel, BorderLayout.CENTER);
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton startButton = new JButton("Start/Restart");
        startButton.addActionListener(e -> {
            resizeFrame();
            changeParticles();
            changeCycles();
            panel.updateParticles();
        });

        JLabel modeLabel = new JLabel("Mode:");
        String[] modes = {"Sequential", "Parallel"};
        modeComboBox = new JComboBox<>(modes);

        buttonPanel.add(startButton);
        buttonPanel.add(modeLabel);
        buttonPanel.add(modeComboBox);
        controlPanel.add(buttonPanel, BorderLayout.NORTH);

        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel widthLabel = new JLabel("Width:");
        widthField = new JTextField(String.valueOf(DEFAULT_WIDTH), 5);

        JLabel heightLabel = new JLabel("Height:");
        heightField = new JTextField(String.valueOf(DEFAULT_HEIGHT), 5);

        JLabel particlesLabel = new JLabel("Particles:");
        particlesField = new JTextField(String.valueOf(DEFAULT_NUM_PARTICLES), 5);

        JLabel cyclesLabel = new JLabel("Cycles:");
        cyclesField = new JTextField(String.valueOf(DEFAULT_NUM_CYCLES), 5);

        JLabel timeLabel = new JLabel("Time:");
        elapsedTimeLabel = new JLabel("0 ms");

        sizePanel.add(widthLabel);
        sizePanel.add(widthField);
        sizePanel.add(heightLabel);
        sizePanel.add(heightField);
        sizePanel.add(particlesLabel);
        sizePanel.add(particlesField);
        sizePanel.add(cyclesLabel);
        sizePanel.add(cyclesField);
        sizePanel.add(timeLabel);
        sizePanel.add(elapsedTimeLabel);
        controlPanel.add(sizePanel, BorderLayout.WEST);
        add(controlPanel, BorderLayout.NORTH);
    }

    public void updateElapsedTime(long elapsedTime) {
        SwingUtilities.invokeLater(() -> {
            elapsedTimeLabel.setText(elapsedTime + " ms");
        });
    }

    private void resizeFrame() {
        int width = Integer.parseInt(widthField.getText());
        int height = Integer.parseInt(heightField.getText());
        setSize(width, height);
        panel.setPreferredSize(new Dimension(width, height));
        panel.changePanelSize(width, height);
        panel.revalidate();
    }

    private void changeParticles() {
        int numParticles = Integer.parseInt(particlesField.getText());
        panel.changeParticleCount(numParticles);
    }

    private void changeCycles() {
        int numCycles = Integer.parseInt(cyclesField.getText());
        panel.changeCycles(numCycles);
    }

    public String getMode() {
        return (String) modeComboBox.getSelectedItem();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Frame frame = new Frame();
            frame.setVisible(true);
        });
    }
}