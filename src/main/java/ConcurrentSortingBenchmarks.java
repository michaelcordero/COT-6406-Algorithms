import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class ConcurrentSortingBenchmarks {

    final static ConcurrentHashMap<Integer, Long> merge_times = new ConcurrentHashMap<>();
    final static ConcurrentHashMap<Integer, Long> insertion_times = new ConcurrentHashMap<>();

    enum SortType {
        MERGE, INSERTION
    }

    static class ResultPair {
        Integer elements;
        Long time;
        SortType type;

        ResultPair(Integer elements, Long time, SortType type) {
            this.elements = elements;
            this.time = time;
            this.type = type;
        }
    }

    static ResultPair computeSortTime(int[] slice, SortType type) {
        long start_time = System.currentTimeMillis();
        switch (type) {
            case MERGE -> MergeSort.mergeSort(slice, slice.length);
            case INSERTION -> InsertionSort.insertionSort(slice);
        }
        long end_time = System.currentTimeMillis();
        long completion_time = end_time - start_time;
        return new ResultPair(slice.length, completion_time, type);
    }

    public static void main(String[] args) throws IOException {
        // generate some random numbers
        Random random = new Random(System.currentTimeMillis());
        int[] numbers = new int[100_000];
        for (int i = 0; i < 100_000; i++) {
            numbers[i] = random.nextInt(1, 100_000);
        }
        // declare N elements array
        List<Integer> elements = List.of(10_000, 20_000, 30_000, 40_000, 50_000, 60_000, 70_000, 80_000, 90_000, 100_000);
        // executor service
        // Compute Start Time
        long compute_start_time = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newWorkStealingPool()) {
//            System.out.printf("Running on %d threads\n", processors);
            // record merge sort times for 10, 1000, 10_000, 100_000
            List<Callable<ResultPair>> composite_sorts = new ArrayList<>();
            for (Integer n : elements) {
                // copy slice of array containing elements to be sorted
                int[] m_slice = Arrays.copyOfRange(numbers, 0, n);
                int[] i_slice = Arrays.copyOfRange(numbers, 0, n);
                composite_sorts.add(() -> computeSortTime(m_slice, SortType.MERGE));
                composite_sorts.add(() -> computeSortTime(i_slice, SortType.INSERTION));
            }

            try {
                List<Future<ResultPair>> composite_futures = executor.invokeAll(composite_sorts);
                for (Future<ResultPair> f : composite_futures) {
                    ResultPair rp = f.get();
                    switch (rp.type) {
                        case MERGE -> merge_times.put(rp.elements, rp.time);
                        case INSERTION -> insertion_times.put(rp.elements, rp.time);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        // Compute End Time
        long compute_end_time = System.currentTimeMillis();
        long compute_completion_time = compute_end_time - compute_start_time;
        System.out.printf("Compute Completion Time: %d", compute_completion_time);

        // create XY Chart
        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(800)
                .title("Asymptotic Analysis")
                .xAxisTitle("N # of elements")
                .yAxisTitle("Time in ms")
                .build();
        // add series to chart
        chart.addSeries("Merge Sort O(n lg n)", Collections.list(merge_times.keys()).stream().mapToDouble(Integer::doubleValue).toArray(),
                merge_times.values().stream().mapToDouble(Long::doubleValue).toArray());
        chart.addSeries("Insertion Sort O(n^2)", Collections.list(insertion_times.keys()).stream().mapToDouble(Integer::doubleValue).toArray(),
                insertion_times.values().stream().mapToDouble(Long::doubleValue).toArray());
        // display chart
        new SwingWrapper<>(chart).displayChart();
        BitmapEncoder.saveBitmap(chart, "src/main/resources/asymptotic", BitmapEncoder.BitmapFormat.PNG);
    }
}
