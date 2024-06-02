import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Start2 {
    public static ExecutorService executor = Executors.newFixedThreadPool(5);
    public static void main(String[] args) {
        int numParticles = 500;
        int numCycles = 10000;
        int width = 800;
        int height = 600;
        String mode = "Parallel"; // "Sequential" or "Parallel"
        long maxRunTime = 360000; // (for testing) 60000ms=1min

        boolean withinTimeLimit = true;
        while (withinTimeLimit) {
            Map<Integer, ParticleProperties> initialParticleProperties = createParticles(numParticles, width, height);
            long cycleTime = runSimulation(initialParticleProperties, numCycles, width, height, mode);
            if (cycleTime > maxRunTime) {
                withinTimeLimit = false;
            }
            numParticles += 500; // (for testing)
        }
        System.out.println("Total time exceeded, program stopped.");
    }

    private static Map<Integer, ParticleProperties> createParticles(int numParticles, int width, int height) {
        Map<Integer, ParticleProperties> initialParticleProperties = new HashMap<>();
        Random rand = new Random(42); // seed for random instance so positions are always the same (for testing)

        for (int i = 0; i < numParticles; i++) {
            PVector pos = new PVector(rand.nextInt(width), rand.nextInt(height));
            PVector speed = new PVector(rand.nextFloat() * 10 - 5, rand.nextFloat() * 10 - 5);
            Color color = rand.nextBoolean() ? Color.RED : Color.BLUE;
            initialParticleProperties.put(i, new ParticleProperties(pos, speed, color));
        }
        return initialParticleProperties;
    }

    private static long runSimulation(Map<Integer, ParticleProperties> initialParticleProperties, int numCycles, int width, int height, String mode) {
        long startTime = System.currentTimeMillis();  // record start time
        System.out.println("Selected mode: " + mode);
        System.out.println("Number of particles: " + initialParticleProperties.size());

        List<Particle> particles = createParticlesFromProperties(initialParticleProperties);
        Particle firstParticle = particles.get(0);
        System.out.println("Initial location and speed of first particle: " + firstParticle.pos.x + ", " + firstParticle.pos.y + ", " + firstParticle.vel.x + ", " + firstParticle.vel.y); // (for testing)

        for (int cycle = 0; cycle < numCycles; cycle++) {
            updateParticles(particles, width, height, mode);
        }

        long endTime = System.currentTimeMillis(); // record stop time
        long totalTime = endTime - startTime;
        System.out.println("Total time for " + numCycles + " cycles: " + totalTime + " ms");
        return totalTime;
    }

    private static List<Particle> createParticlesFromProperties(Map<Integer, ParticleProperties> initialParticleProperties) {
        List<Particle> particles = new ArrayList<>();
        for (Map.Entry<Integer, ParticleProperties> entry : initialParticleProperties.entrySet()) {
            ParticleProperties properties = entry.getValue();
            float speedMagnitude = (float) Math.sqrt(properties.speed.x * properties.speed.x + properties.speed.y * properties.speed.y);
            particles.add(new Particle(properties.pos, speedMagnitude, properties.color));
        }
        return particles;
    }

    private static void updateParticles(List<Particle> particles, int width, int height, String mode) {
        switch (mode) {
            case "Sequential":
                updateParticlesSequentially(particles, width, height);
                break;
            case "Parallel":
                updateParticlesInParallel(particles, width, height);
                break;
        }
    }

    private static void updateParticlesSequentially(List<Particle> particles, int width, int height) {
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).update(particles.toArray(new Particle[0]), i, width, height);
        }
    }

    private static void updateParticlesInParallel(List<Particle> particles, int width, int height) {
        int numThreads = 5;
        final Particle[] particlesArray = particles.toArray(new Particle[0]); // create thread pool

        for (int i = 0; i < numThreads; i++) {
            final int start = i * (particles.size() / numThreads);
            final int end = (i == numThreads - 1) ? particles.size() : (i + 1) * (particles.size() / numThreads);
            executor.submit(() -> {
                for (int j = start; j < end; j++) {
                    particlesArray[j].update(particlesArray, j, width, height);
                }
            });
        }
    }

    // helper class to store initial particle properties
    private static class ParticleProperties {
        PVector pos;
        PVector speed;
        Color color;

        ParticleProperties(PVector pos, PVector speed, Color color) {
            this.pos = pos;
            this.speed = speed;
            this.color = color;
        }
    }
}