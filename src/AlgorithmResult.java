import java.util.List;

public class AlgorithmResult {
    private List<PageFrame[]> steps;
    private List<Boolean> pageFaults;
    private int totalPageFaults;

    public AlgorithmResult(List<PageFrame[]> steps, List<Boolean> pageFaults, int totalPageFaults) {
        this.steps = steps;
        this.pageFaults = pageFaults;
        this.totalPageFaults = totalPageFaults;
    }

    public List<PageFrame[]> getSteps() {
        return steps;
    }

    public List<Boolean> getPageFaults() {
        return pageFaults;
    }

    public int getTotalPageFaults() {
        return totalPageFaults;
    }
}
