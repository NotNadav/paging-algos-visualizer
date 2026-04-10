import java.util.*;

public class SecondChanceAlgorithm {
    private int frameCount;

    public SecondChanceAlgorithm(int frameCount) {
        this.frameCount = frameCount;
    }

    public AlgorithmResult execute(int[] referenceString) {
        List<PageFrame[]> steps = new ArrayList<>();
        List<Boolean> pageFaults = new ArrayList<>();
        PageFrame[] frames = new PageFrame[frameCount];
        int pageFaultCount = 0;
        int clockHand = 0;
        int filledFrames = 0;

        for (int page : referenceString) {
            boolean pageFault = true;

            for (int i = 0; i < frameCount; i++) {
                if (frames[i] != null && frames[i].getPageNumber() == page) {
                    pageFault = false;
                    frames[i].setReferenceBit(true);
                    break;
                }
            }

            if (pageFault) {
                pageFaultCount++;

                if (filledFrames < frameCount) {
                    frames[filledFrames] = new PageFrame(page);
                    frames[filledFrames].setReferenceBit(true);
                    filledFrames++;
                } else {
                    while (true) {
                        if (frames[clockHand].getReferenceBit()) {
                            frames[clockHand].setReferenceBit(false);
                            clockHand = (clockHand + 1) % frameCount;
                        } else {
                            frames[clockHand] = new PageFrame(page);
                            frames[clockHand].setReferenceBit(true);
                            clockHand = (clockHand + 1) % frameCount;
                            break;
                        }
                    }
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
