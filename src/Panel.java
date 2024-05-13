import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Panel extends JPanel {
    // panel dimension
    private int WIDTH = 780;
    private int HEIGHT = 490;

    private Frame frame;
    private List<Particle> particles;
    private boolean renderParticles;
    private int numCycles;
    private String previousMode;
    private List<PVector> initialLocations;

    public Panel(Frame frame, boolean renderParticles, int numParticles, int numCycles) {
        this.frame = frame; // initialize frame reference
        setBackground(Color.BLACK);
        this.renderParticles = renderParticles; // set rendering mode for particles
        this.numCycles = numCycles; // set number of cycles
        particles = new ArrayList<>(); // initialize list to store particles
        initialLocations = new ArrayList<>(); // initialize list to store initial particle positions (for testing)
        previousMode = frame.getMode(); // initialize previous mode

        if (renderParticles) { // rendering particles is enabled
            createParticles(numParticles);
            if (numCycles > 0) {
                startSimulation();
            }
        }
    }

    private void createParticles(int numParticles) {
        particles.clear();
        Random rand = new Random();

        // if initialLocations list is empty or mode has changed (for testing)
        if (initialLocations.isEmpty() || frame.getMode().equals(previousMode)) {
            initialLocations.clear();
            for (int i = 0; i < numParticles; i++) {
                PVector pos = new PVector(rand.nextInt(WIDTH), rand.nextInt(HEIGHT)); // generate random initial position
                initialLocations.add(pos.copy()); // store initial position
                Color color = rand.nextBoolean() ? Color.RED : Color.BLUE; // randomly assign color
                particles.add(new Particle(pos, 100, color)); // create particle
            }
            previousMode = frame.getMode(); // update previous mode
        }
        // if mode is same as before, reuse previous initial positions (for testing)
        else {
            for (int i = 0; i < numParticles; i++) {
                PVector pos = initialLocations.get(i % initialLocations.size());
                Color color = rand.nextBoolean() ? Color.RED : Color.BLUE;
                particles.add(new Particle(pos.copy(), 100, color));
            }
        }
    }

    private void startSimulation() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                String currentMode = frame.getMode(); // get current mode
                if (!currentMode.equals(previousMode)) {
                    previousMode = currentMode; // update previous mode if changed
                }
                Particle firstParticle = particles.get(0);
                System.out.println("Initial location of first particle: " + firstParticle.pos.x + ", " + firstParticle.pos.y); // (for testing)
                System.out.println("Selected mode: " + currentMode);
                System.out.println("Number of particles: " + particles.size());

                long startTime = System.currentTimeMillis(); // record start time
                for (int cycle = 0; cycle < numCycles; cycle++) {
                    Mode.updateParticles(Panel.this); // update particles
                    repaint(); // repaint the panel
                    Thread.sleep(1000 / 60); // sleep to maintain frame rate 60fps
                }
                long endTime = System.currentTimeMillis(); // record end time
                long totalTime = endTime - startTime;
                System.out.println("Total time for " + numCycles + " cycles: " + totalTime + " ms");
                frame.updateElapsedTime(totalTime);
                return null;
            }
        };
        worker.execute();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // call superclass paintComponent method
        if (particles != null) {
            for (Particle particle : particles) {
                g.setColor(particle.color);
                g.fillOval((int) (particle.pos.x - 2.5f), (int) (particle.pos.y - 2.5f), 5, 5);
            }
        }
    }

    // ----functions to make buttons work with class Frame-------
    public void changeParticleCount(int numParticles) {
        if (particles != null) {
            createParticles(numParticles);
            if (numCycles > 0) {
                startSimulation();
            }
            repaint();
        }
    }

    public void changePanelSize(int width, int height) {
        WIDTH = width - 20;
        HEIGHT = height - 110;
    }

    public void changeCycles(int numCycles) {
        this.numCycles = numCycles;
        if (renderParticles && numCycles > 0) {
            startSimulation();
        }
    }

    public Frame getFrame() {
        return frame;
    }

    public List<Particle> getParticles() {
        return particles;
    }
    //------------------------------------------------------------

    public void updateParticles() {
        Mode.updateParticles(this);
    }
}