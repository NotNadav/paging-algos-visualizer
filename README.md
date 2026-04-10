# paging algorithm visualizer 

just a small side project to visualize how memory paging works (FIFO, LRU, etc) with a modern look.

## how to run it

1. **compile everything:**
   ```bash
   javac src/*.java
   ```

2. **run the app:**
   ```bash
   java -cp src PagingVisualizer
   ```

## how to use
- throw in some numbers for the **reference string** (like `7 0 1 2 0 3`)
- set your **working set size** (1-10 frames)
- pick an **algorithm** from the list
- hit **"Run Simulation"** and watch it go 
