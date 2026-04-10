import java.util.*;

public class OPTAlgorithm {
    private int frameCount;

    public OPTAlgorithm(int frameCount) {
        this.frameCount = frameCount;
    }

    public AlgorithmResult execute(int[] referenceString) {
        List<PageFrame[]> steps = new ArrayList<>();
        List<Boolean> pageFaults = new ArrayList<>();
        PageFrame[] frames = new PageFrame[frameCount];
        int pageFaultCount = 0;

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            boolean pageFault = true;

            // Check if page is already in a frame
            for (int j = 0; j < frameCount; j++) {
                if (frames[j] != null && frames[j].getPageNumber() == page) {
                    pageFault = false;
                    break;
                }
            }

            if (pageFault) {
                pageFaultCount++;

                // Try to find an empty slot
                boolean placed = false;
                for (int j = 0; j < frameCount; j++) {
                    if (frames[j] == null) {
                        frames[j] = new PageFrame(page);
                        placed = true;
                        break;
                    }
                }

                // If no empty slots, use the Optimal algorithm to replace
                if (!placed) {
                    int replaceIndex = findOptimalReplacement(frames, referenceString, i + 1);
                    frames[replaceIndex] = new PageFrame(page);
                }
            }

            // Save state for visualization
            PageFrame[] currentState = new PageFrame[frameCount];
            for (int j = 0; j < frameCount; j++) {
                currentState[j] = frames[j] != null ? frames[j].copy() : null;
            }
            steps.add(currentState);
            pageFaults.add(pageFault);
        }

        return new AlgorithmResult(steps, pageFaults, pageFaultCount);
    }

    private int findOptimalReplacement(PageFrame[] frames, int[] referenceString, int startIndex) {
        int furthestIndex = -1;
        int replaceIndex = 0;

        for (int i = 0; i < frames.length; i++) {
            int nextUse = Integer.MAX_VALUE;
            
            // Look ahead in the reference string
            for (int k = startIndex; k < referenceString.length; k++) {
                if (frames[i].getPageNumber() == referenceString[k]) {
                    nextUse = k;
                    break;
                }
            }

            // If a page is never used again, replace it immediately
            if (nextUse == Integer.MAX_VALUE) {
                return i;
            }

            // Otherwise, replace the one used furthest in the future
            if (nextUse > furthestIndex) {
                furthestIndex = nextUse;
                replaceIndex = i;
            }
        }

        return replaceIndex;
    }
}
