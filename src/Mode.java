import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Mode {
    public static final int numThreads = 5;
    public static ExecutorService executor = null;

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
        List<Particle> particles = panel.getParticles(); // create thread pool
        Particle[] particlesArray = particles.toArray(new Particle[0]);
        List<Callable<Void>> updateParticleTasks = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final int start = i * (particles.size() / numThreads);
            final int end = (i == numThreads - 1) ? particles.size() : (i + 1) * (particles.size() / numThreads);
            updateParticleTasks.add(new UpdateTask(start, end, particlesArray, panel.getWidth(), panel.getHeight()));
        }

        try {
            executor.invokeAll(updateParticleTasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}