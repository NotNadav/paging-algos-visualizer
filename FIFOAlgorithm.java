import java.util.*;

public class FIFOAlgorithm {
    private int frameCount;

    public FIFOAlgorithm(int frameCount) {
        this.frameCount = frameCount;
    }

    public AlgorithmResult execute(int[] referenceString) {
        List<PageFrame[]> steps = new ArrayList<>();
        List<Boolean> pageFaults = new ArrayList<>();
        PageFrame[] frames = new PageFrame[frameCount];
        Queue<Integer> fifoQueue = new LinkedList<>();
        int pageFaultCount = 0;

        for (int page : referenceString) {
            boolean pageFault = true;

            for (int i = 0; i < frameCount; i++) {
                if (frames[i] != null && frames[i].getPageNumber() == page) {
                    pageFault = false;
                    break;
                }
            }

            if (pageFault) {
                pageFaultCount++;

                boolean placed = false;
                for (int i = 0; i < frameCount; i++) {
                    if (frames[i] == null) {
                        frames[i] = new PageFrame(page);
                        fifoQueue.add(i);
                        placed = true;
                        break;
                    }
                }

                if (!placed) {
                    int oldestIndex = fifoQueue.poll();
                    frames[oldestIndex] = new PageFrame(page);
                    fifoQueue.add(oldestIndex);
                }
            }

            PageFrame[] currentState = new PageFrame[frameCount];
            for (int i = 0; i < frameCount; i++) {
                currentState[i] = frames[i] != null ? frames[i].copy() : null;
            }
            steps.add(currentState);
            pageFaults.add(pageFault);
        }

        return new AlgorithmResult(steps, pageFaults, pageFaultCount);
    }
}
