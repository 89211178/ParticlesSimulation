import java.util.concurrent.Callable;

public class UpdateTask implements Callable<Void> {
    int start;
    int end;
    Particle[] particlesArray;
    int width;
    int height;

    public UpdateTask(int start, int end, Particle[] particlesArray, int width, int height) {
        this.start = start;
        this.end = end;
        this.particlesArray = particlesArray;
        this.width = width;
        this.height = height;
    }

    @Override
    public Void call() throws Exception {
        for (int j = start; j < end; j++) {
            particlesArray[j].update(particlesArray, j, width, height);
        }
        return null;
    }
}