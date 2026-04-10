import java.util.*;

public class LRUAlgorithm {
    private int frameCount;

    public LRUAlgorithm(int frameCount) {
        this.frameCount = frameCount;
    }

    public AlgorithmResult execute(int[] referenceString) {
        List<PageFrame[]> steps = new ArrayList<>();
        List<Boolean> pageFaults = new ArrayList<>();
        PageFrame[] frames = new PageFrame[frameCount];
        int pageFaultCount = 0;
        int timestamp = 0;

        for (int page : referenceString) {
            boolean pageFault = true;
            timestamp++;

            for (int i = 0; i < frameCount; i++) {
                if (frames[i] != null && frames[i].getPageNumber() == page) {
                    pageFault = false;
                    frames[i].setTimestamp(timestamp);
                    break;
                }
            }

            if (pageFault) {
                pageFaultCount++;

                boolean placed = false;
                for (int i = 0; i < frameCount; i++) {
                    if (frames[i] == null) {
                        frames[i] = new PageFrame(page);
                        frames[i].setTimestamp(timestamp);
                        placed = true;
                        break;
                    }
                }

                if (!placed) {
                    int lruIndex = 0;
                    int minTimestamp = frames[0].getTimestamp();

                    for (int i = 1; i < frameCount; i++) {
                        if (frames[i].getTimestamp() < minTimestamp) {
                            minTimestamp = frames[i].getTimestamp();
                            lruIndex = i;
                        }
                    }

                    frames[lruIndex] = new PageFrame(page);
                    frames[lruIndex].setTimestamp(timestamp);
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
