import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class BalancingBinaryTreeBenchmarks {
    final static Map<Integer, Long> chang_iyengar = new ConcurrentHashMap<>();
    final static Map<Integer, Long> uncredited = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        // generate some random numbers
        Random random = new Random(System.currentTimeMillis());
        int[] numbers = new int[1_000_000];
        for (int i = 0; i < 1_000_000; i++) {
            numbers[i] = random.nextInt(1, 100_000);
        }
        // declare N elements array
        List<Integer> elements = List.of(10_000, 50_000, 100_000, 250_000, 500_000, 1_000_000);
        // Compute Start Time
        long compute_start_time = System.currentTimeMillis();
        // record merge sort times for 10, 1000, 10_000, 100_000
        for (Integer n : elements) {
            // pull numbers
            int[] sliced = Arrays.copyOfRange(numbers, 0, n);
            BinarySearchTree<Integer,Integer> bst = new BinarySearchTree<>();
            for (int i = 0; i < n; i++) {
                bst.put(sliced[i], sliced[i]);
            }
            long start_time = System.currentTimeMillis();
            bst.chang_iyengar();
            long end_time = System.currentTimeMillis();
            long completion_time = end_time - start_time;
            chang_iyengar.put(n, completion_time);
        }
        // record insertion sort times for 10, 1000, 10_000, 100_000
        for (Integer n : elements) {
            int[] sliced = Arrays.copyOfRange(numbers, 0, n);
            BinarySearchTree<Integer,Integer> bstu = new BinarySearchTree<>();
            for (int i = 0; i < n; i++) {
                bstu.put(sliced[i], sliced[i]);
            }
            long start_time = System.currentTimeMillis();
            bstu.balance();
            long end_time = System.currentTimeMillis();
            long completion_time = end_time - start_time;
            uncredited.put(n, completion_time);
        }
        // Compute End Time
        long compute_end_time = System.currentTimeMillis();
        long compute_completion_time = compute_end_time - compute_start_time;
        System.out.printf("Compute completion time: %dms", compute_completion_time);

        // create XY Chart
        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(800)
                .title("Asymptotic Analysis")
                .xAxisTitle("N # of elements")
                .yAxisTitle("Time in ms")
                .build();
        // add series to chart
        chart.addSeries("Chang-Iyengar", chang_iyengar.keySet().stream().mapToDouble(Integer::doubleValue).toArray(),
                chang_iyengar.values().stream().mapToDouble(Long::doubleValue).toArray());
        chart.addSeries("Uncredited", uncredited.keySet().stream().mapToDouble(Integer::doubleValue).toArray(),
                uncredited.values().stream().mapToDouble(Long::doubleValue).toArray());
        // display chart
        new SwingWrapper<>(chart).displayChart();
        BitmapEncoder.saveBitmap(chart, "src/main/resources/asymptotic", BitmapEncoder.BitmapFormat.PNG);
    }
}
