package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;
import java.awt.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
public class MultithreadingDemo {

    // Record the execution time of each task, two-dimensional array: [Number of tasks][0: single-threaded time, 1: multithreaded time]
    private long[][] taskTimes;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MultithreadingDemo().createAndShowGUI());
    }

    //Create the main panel
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Efficient Multithreading in Java: Exploring Thread Management and Synchronization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        // Title section
        JLabel titleLabel = new JLabel("Efficient Multithreading in Java", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Function tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Thread Basics", createThreadBasicsPanel());
        tabbedPane.add("Thread Pool Management", createThreadPoolPanel());
        tabbedPane.add("Synchronization Demonstration", createSynchronizationPanel());
        tabbedPane.add("Performance Analysis", createPerformanceAnalysisPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createThreadBasicsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();

        JButton startThreadsButton = new JButton("Start thread");
        JButton runPerformanceTestButton = new JButton("Operational performance test");

        buttonPanel.add(startThreadsButton);
        buttonPanel.add(runPerformanceTestButton);

        JTextArea logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // Enable the thread button function
        startThreadsButton.addActionListener(e -> startThreads(logArea, progressBar));

        // Performance test button function
        runPerformanceTestButton.addActionListener(e -> {
            // An input box is displayed for the user to enter the number of tasks
            String input = JOptionPane.showInputDialog("Enter the number of tasks:");
            int taskCount;
            try {
                taskCount = Integer.parseInt(input); // 获取任务数量
            } catch (NumberFormatException ex) {
                logArea.append("Invalid input. Please enter a valid number.\n");
                return;
            }

            //Calls the performance test method and passes in the number of tasks
            runPerformanceTest(logArea, taskCount);
        });

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(logScrollPane, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        return panel;
    }

    // Create a thread pool management module
    private JPanel createThreadPoolPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel();

        JLabel poolTypeLabel = new JLabel("Thread pool type:");
        JComboBox<String> poolTypeComboBox = new JComboBox<>(new String[]{"Fixed thread pool", "Cache thread pool"});
        JButton executeTasksButton = new JButton("Perform a task");

        controlPanel.add(poolTypeLabel);
        controlPanel.add(poolTypeComboBox);
        controlPanel.add(executeTasksButton);

        JTextArea logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        // Perform the task button function
        executeTasksButton.addActionListener(e -> executeTasks(poolTypeComboBox.getSelectedItem().toString(), logArea, progressBar));

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(logScrollPane, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        return panel;
    }

    //Perform the task button function
    private JPanel createSynchronizationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();

        JButton simulateWithoutSyncButton = new JButton("Non-synchronous simulation");
        JButton simulateWithSyncButton = new JButton("Synchronous simulation");

        buttonPanel.add(simulateWithoutSyncButton);
        buttonPanel.add(simulateWithSyncButton);

        JTextArea logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        // There is no synchronous analog button function
        simulateWithoutSyncButton.addActionListener(e -> simulateWithoutSynchronization(logArea));
        // There is synchronous analog button function
        simulateWithSyncButton.addActionListener(e -> simulateWithSynchronization(logArea));

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(logScrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Create a performance analysis module

    private JPanel createPerformanceAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton showChartButton = new JButton("Show Performance Chart");
        JTextArea logArea = new JTextArea(10, 50);
        logArea.setEditable(false);

        // Show Performance Chart button functionality
        showChartButton.addActionListener(e -> {
            // An input box is displayed for the user to enter the number of tasks
            String input = JOptionPane.showInputDialog("Enter the number of tasks:");
            int taskCount;
            try {
                taskCount = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                logArea.append("Invalid input. Please enter a valid number.\n");
                return;
            }

            // Perform performance tests
            runPerformanceTest(logArea, taskCount); // Perform performance tests and update taskTimes

            // Generate a chart using actual time values
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (int i = 0; i < taskCount; i++) {
                dataset.addValue(taskTimes[i][0], "Single Thread", "Task " + (i + 1));
                dataset.addValue(taskTimes[i][1], "Multi-Thread", "Task " + (i + 1));
            }

            // Create a bar chart (Bar Chart)
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Performance Analysis",
                    "Task",
                    "Time (ms)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            //  (Line Chart)
            XYSeriesCollection lineDataset = new XYSeriesCollection();
            XYSeries singleThreadLine = new XYSeries("Single Thread");
            XYSeries multiThreadLine = new XYSeries("Multi-Thread");

            // Create an X-axis coordinate (task number) for each task, and a Y-axis coordinate for the corresponding execution time
            for (int i = 0; i < taskCount; i++) {
                singleThreadLine.add(i + 1, taskTimes[i][0]);  // Single thread time
                multiThreadLine.add(i + 1, taskTimes[i][1]);   // Multithread time
            }

            lineDataset.addSeries(singleThreadLine);
            lineDataset.addSeries(multiThreadLine);

            JFreeChart lineChart = ChartFactory.createXYLineChart(
                    "Performance Line Chart",
                    "Task",
                    "Time (ms)",
                    lineDataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            // Create Scatter plots
            XYSeriesCollection scatterDataset = new XYSeriesCollection();
            XYSeries singleThreadScatter = new XYSeries("Single Thread");
            XYSeries multiThreadScatter = new XYSeries("Multi-Thread");

            // Create an X-axis coordinate (task number) for each task, and a Y-axis coordinate for the corresponding execution time
            for (int i = 0; i < taskCount; i++) {
                singleThreadScatter.add(i + 1, taskTimes[i][0]);
                multiThreadScatter.add(i + 1, taskTimes[i][1]);
            }

            scatterDataset.addSeries(singleThreadScatter);
            scatterDataset.addSeries(multiThreadScatter);

            JFreeChart scatterChart = ChartFactory.createScatterPlot(
                    "Performance Scatter Plot",
                    "Task",
                    "Time (ms)",
                    scatterDataset
            );

            // Create a window to display the chart
            JFrame chartFrame = new JFrame("Performance Charts");
            chartFrame.setLayout(new GridLayout(3, 1));

            //Add chart to window
            chartFrame.add(new ChartPanel(barChart));
            chartFrame.add(new ChartPanel(lineChart));
            chartFrame.add(new ChartPanel(scatterChart));

            // Set the window size and display
            chartFrame.setSize(800, 900);
            chartFrame.setVisible(true);
        });

        panel.add(showChartButton, BorderLayout.NORTH);
        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        return panel;
    }






    // Start thread function: dynamically update the progress bar
    private void startThreads(JTextArea logArea, JProgressBar progressBar) {
        // Let the user select the number of tasks through the input box
        String input = JOptionPane.showInputDialog("Please enter the number of tasks:");
        if (input == null || input.isEmpty()) {
            logArea.append("The number of the test cannot be empty, the task deletes\n");
            return;
        }

        // Try to convert the input to an integer
        int taskCount;
        try {
            taskCount = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            logArea.append("The input is invalid and must be an integer.\n");
            return;
        }

        if (taskCount <= 0) {
            logArea.append("The number of tasks must be greater than 0.\n");
            return;
        }

        logArea.append("Start up " + taskCount + " tasks...\n");

        // Create a fixed-size thread pool that is sized based on the number of tasks
        ExecutorService executor = Executors.newFixedThreadPool(taskCount);

        // Set the maximum value of the progress bar to the number of tasks
        progressBar.setMinimum(0);
        progressBar.setMaximum(taskCount);
        progressBar.setValue(0);

        // Submit a specified number of tasks
        for (int i = 1; i <= taskCount; i++) {
            int threadId = i;
            executor.submit(() -> {
                logArea.append("thread " + threadId + " is running...\n");
                try {
                    Thread.sleep(1000); // 模拟任务
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logArea.append("thread " + threadId + " finished\n");

                // Update the progress bar (to be done in the event scheduler thread)
                SwingUtilities.invokeLater(() -> progressBar.setValue(progressBar.getValue() + 1));
            });
        }

        // Close thread pool
        executor.shutdown();
    }

    //Performance test function

    private long[] runPerformanceTest(JTextArea logArea, int taskCount) {
        logArea.append("Start performance testing with " + taskCount + " tasks...\n");

        // Define test data
        int dataSize = taskCount * 10_000; // Adjust the data size based on the number of tasks
        int[] data = new int[dataSize];
        for (int i = 0; i < dataSize; i++) {
            data[i] = ThreadLocalRandom.current().nextInt(0, dataSize); // Generate random numbers in the range
        }

        // Initializes the two-dimensional array taskTimes to hold the time of each subtask
        taskTimes = new long[taskCount][2]; // [任务数][0:单线程时间, 1:多线程时间]

        long totalSingleThreadTime = 0;
        long totalMultiThreadTime = 0;

        // Single-threaded sort and multithreaded sort
        for (int i = 0; i < taskCount; i++) {
            // Single-threaded task execution
            long singleThreadStart = System.currentTimeMillis();
            Arrays.sort(data.clone()); // Clone data to avoid modifying the original array
            long singleThreadEnd = System.currentTimeMillis();
            taskTimes[i][0] = singleThreadEnd - singleThreadStart; // Single thread time
            totalSingleThreadTime += taskTimes[i][0]; // Cumulative single thread time

            // Multithreaded task execution
            long multiThreadStart = System.currentTimeMillis();
            Arrays.parallelSort(data.clone());
            long multiThreadEnd = System.currentTimeMillis();
            taskTimes[i][1] = multiThreadEnd - multiThreadStart; // Multithread time
            totalMultiThreadTime += taskTimes[i][1]; // Cumulative multithread time

            // Output the execution time of each task
            logArea.append("Task " + (i + 1) + " - Single-threaded time: " + taskTimes[i][0] + " ms\n");
            logArea.append("Task " + (i + 1) + " - Multithreaded time: " + taskTimes[i][1] + " ms\n");
        }

        // Total return time
        return new long[] {totalSingleThreadTime, totalMultiThreadTime};  // Returns the total time of single and multithreaded threads
    }

    // Execute thread pool tasks
    private void executeTasks(String poolType, JTextArea logArea, JProgressBar progressBar) {
        //The input box is displayed to obtain the number of tasks entered by the user
        String input = JOptionPane.showInputDialog(null, "Please enter the number of tasks:", "Number of input tasks", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.trim().isEmpty()) {
            logArea.append("Number of invalid tasks, using the default:10\n");
            input = "10";
        }

        int taskCount;
        try {
            taskCount = Integer.parseInt(input.trim()); // Converts the number of tasks entered to an integer
        } catch (NumberFormatException e) {
            logArea.append("Number of invalid tasks, using the default:10\n");
            taskCount = 10; // If the input is invalid, the default value 10 is used
        }

        logArea.append("Number of tasks used: " + taskCount + " Perform a task...\n");

        // Set the maximum value of the progress bar to the number of tasks
        progressBar.setMinimum(0);
        progressBar.setMaximum(taskCount);
        progressBar.setValue(0);

        // The input box is displayed to obtain the thread pool type selected by the user
        String[] poolTypes = {"Fixed thread pool", "Cached thread pool"};
        String poolChoice = (String) JOptionPane.showInputDialog(
                null, "Select the thread pool type", "Thread pool type",
                JOptionPane.QUESTION_MESSAGE, null, poolTypes, poolTypes[0]
        );
        if (poolChoice == null) {
            poolChoice = "Fixed thread pool"; //
        }

        ExecutorService executor;
        if ("Fixed thread pool".equals(poolChoice)) {
            executor = Executors.newFixedThreadPool(4);
        } else {
            executor = Executors.newCachedThreadPool();
        }


        for (int i = 1; i <= taskCount; i++) {
            int taskId = i;
            executor.submit(() -> {
                logArea.append("task " + taskId + " running...\n");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logArea.append("task " + taskId + " finished.\n");


                SwingUtilities.invokeLater(() -> progressBar.setValue(progressBar.getValue() + 1));
            });
        }

        executor.shutdown();
    }

    private void showSyncPerformanceChart(JTextArea logArea) {
        logArea.append("synchronized method耗时: " + testSyncMethodPerformance("synchronized") + " ms\n");
        logArea.append("ReentrantLock method耗时: " + testSyncMethodPerformance("ReentrantLock") + " ms\n");


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(testSyncMethodPerformance("synchronized"), "synchronized", "task");
        dataset.addValue(testSyncMethodPerformance("ReentrantLock"), "ReentrantLock", "task");

        JFreeChart chart = ChartFactory.createBarChart(
                "Performance comparison of synchronization methods",
                "Synchronization method",
                "time-consuming (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame("Performance comparison diagram of synchronization methods");
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }
    // Test the performance of different synchronization methods
    private long testSyncMethodPerformance(String methodType) {
        final int taskCount = 1000;
        final int threadCount = 4;

        SharedResourceSync resource = "ReentrantLock".equals(methodType) ? new SharedResourceReentrantLock() : new SharedResourceSync();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < taskCount; j++) {
                    resource.increment();
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();

        return end - start;
    }
    // ReentrantLock 实现
    private static class SharedResourceReentrantLock extends SharedResourceSync {
        private final ReentrantLock lock = new ReentrantLock();

        @Override
        public void increment() {
            lock.lock();
            try {
                super.increment();
            } finally {
                lock.unlock();
            }
        }
    }
    //No synchronization simulation function

    private void simulateWithoutSynchronization(JTextArea logArea) {
        logArea.append("Simulating without synchronization...\n");
        SharedResource resource = new SharedResource();
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 100; j++) { // Increase the number of cycles to create competition
                    resource.increment();
                    try {
                        Thread.sleep(1); // Simulated thread interleaving
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logArea.append("Final count without synchronization: " + resource.getCount() + "\n");
    }

    // Synchronous simulation function
    private void simulateWithSynchronization(JTextArea logArea) {
        logArea.append("Simulating with synchronization...\n");
        SharedResourceSync resource = new SharedResourceSync();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 100; j++) { // Increase the number of cycles to create competition
                    resource.increment();
                    try {
                        Thread.sleep(1); // Simulated thread interleaving
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logArea.append("Final count with synchronization: " + resource.getCount() + "\n");
    }


    // Shared resource class (no synchronization)
    // None Synchronized shared resource class
    // The shared resource class is not synchronized
    private static class SharedResource implements SharedResourceInterface {
        private int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    // Shared resource class (synchronization)
// There are synchronized shared resource classes
    //There are synchronized shared resource classes
    private static class SharedResourceSync implements SharedResourceInterface {
        private int count = 0;

        public synchronized void increment() {
            count++;
        }

        public synchronized int getCount() {
            return count;
        }
    }
    static class MergeSortTask extends RecursiveAction {
        private final int[] array;
        private final int left;
        private final int right;

        public MergeSortTask(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (left >= right){ return;}

            int mid = (left + right) / 2;
            MergeSortTask leftTask = new MergeSortTask(array, left, mid);
            MergeSortTask rightTask = new MergeSortTask(array, mid + 1, right);

            invokeAll(leftTask, rightTask);
            merge(array, left, mid, right);
        }

        private void merge(int[] array, int left, int mid, int right) {
            int[] temp = new int[right - left + 1];
            int i = left, j = mid + 1, k = 0;

            while (i <= mid && j <= right) {
                temp[k++] = (array[i] <= array[j]) ? array[i++] : array[j++];
            }
            while (i <= mid) {
                temp[k++] = array[i++];
            }
            while (j <= right) {
                temp[k++] = array[j++];
            }

            System.arraycopy(temp, 0, array, left, temp.length);
        }
    }
    private void monitorThreadState(JTextArea logArea) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        final int totalTasks = 10;

        for (int i = 1; i <= totalTasks; i++) {
            int taskId = i;
            executor.submit(() -> {
                logArea.append("task " + taskId + " status: RUNNING\n");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logArea.append("task " + taskId + " status: COMPLETED\n");
            });
        }

        new Timer(500, e -> {
            logArea.append("Number of active threads: " + executor.getActiveCount() + "\n");
            logArea.append("Task queue length: " + executor.getQueue().size() + "\n");
        }).start();
        executor.shutdown();
    }
    private void simulateResourceContention(JTextArea logArea, boolean isSynchronized) {
        logArea.append("Simulating resource contention...\n");

        SharedResourceInterface resource = isSynchronized ? new SharedResourceSync() : new SharedResource();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 5; j++) {
                    resource.increment();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logArea.append("Final count: " + resource.getCount() + "\n");
    }

}