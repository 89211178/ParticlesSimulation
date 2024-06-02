import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Mode {
    public static ExecutorService executor = Executors.newFixedThreadPool(5);
    public static void updateParticles(Panel panel) {
        switch (panel.getFrame().getMode()) {
            case "Sequential":
                updateParticlesSequentially(panel);
                break;
            case "Parallel":
                updateParticlesInParallel(panel);
                break;
        }
    }

    private static void updateParticlesSequentially(Panel panel) {
        List<Particle> particles = panel.getParticles();
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).update(particles.toArray(new Particle[0]), i, panel.getWidth(), panel.getHeight());
        }
    }

    private static void updateParticlesInParallel(Panel panel) {
        int numThreads = 5;
        List<Particle> particles = panel.getParticles(); // create thread pool

        for (int i = 0; i < numThreads; i++) {
            final int start = i * (particles.size() / numThreads); // calculate start index for current thread
            final int end = (i == numThreads - 1) ? particles.size() : (i + 1) * (particles.size() / numThreads); // calculate end index for current thread
            final Particle[] particlesArray = particles.toArray(new Particle[0]); // convert the list of particles to array
            executor.submit(() -> { // submit task to executor
                for (int j = start; j < end; j++) { // iterate over particles assinged for current thread
                    Particle particle = particlesArray[j];
                    particle.update(particlesArray, j, panel.getWidth(), panel.getHeight());
                }
            });
        }
    }
}