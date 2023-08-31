import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class SortingBenchmarks {
    final static ConcurrentHashMap<Integer, Long> merge_times = new ConcurrentHashMap<>();
    final static ConcurrentHashMap<Integer, Long> insertion_times = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // generate some random numbers
        Random random = new Random(System.currentTimeMillis());
        int[] numbers = new int[100_000];
        for (int i = 0; i < 100_000; i++) {
            numbers[i] = random.nextInt(1, 100_000);
        }
        // declare N elements array
        List<Integer> elements = List.of( 10_000, 20_000, 30_000, 40_000, 50_000, 60_000, 70_000,
                80_000, 90_000, 100_000);

        // record merge sort times for 10, 1000, 10_000, 100_000
        for (Integer n : elements) {
            // pull numbers
            int[] sliced = Arrays.copyOfRange(numbers, 0, n);
            long start_time = System.currentTimeMillis();
            MergeSort.mergeSort(sliced, n);
            long end_time = System.currentTimeMillis();
            long completion_time = end_time - start_time;
            merge_times.put(n, completion_time);
        }
        // record insertion sort times for 10, 1000, 10_000, 100_000
        for (Integer n : elements) {
            int[] sliced = Arrays.copyOfRange(numbers, 0, n);
            long start_time = System.currentTimeMillis();
            InsertionSort.insertionSort(sliced);
            long end_time = System.currentTimeMillis();
            long completion_time = end_time - start_time;
            insertion_times.put(n, completion_time);
        }
        // create XY Chart
        XYChart chart = new XYChartBuilder()
                .width(10_000)
                .height(10_000)
                .title("Asymptotic Analysis")
                .xAxisTitle("N # of elements")
                .yAxisTitle("Time in ms")
                .build();
        // add series to chart
        chart.addSeries("Merge Sort", Collections.list(merge_times.keys()).stream().mapToDouble(Integer::doubleValue).toArray(),
                merge_times.values().stream().mapToDouble(Long::doubleValue).toArray());
        chart.addSeries("Insertion Sort", Collections.list(insertion_times.keys()).stream().mapToDouble(Integer::doubleValue).toArray(),
                insertion_times.values().stream().mapToDouble(Long::doubleValue).toArray());
        // display chart
        new SwingWrapper<>(chart).displayChart();

    }
}
