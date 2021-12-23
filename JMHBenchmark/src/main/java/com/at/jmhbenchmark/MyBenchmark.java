/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.at.jmhbenchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 *
 * @author thomp
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2)
@Measurement(iterations = 5)
public class MyBenchmark {

    List<Integer> numList = new ArrayList<>();
    volatile Integer listRange = 100;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

//    @Setup
//    public void setup() {
//        Random rng = new Random();
//        numList = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            int number = rng.nextInt(listRange) + 1;
//            numList.add(number);
//        }
//    }

    @Benchmark
    public int[] bubbleSort(){
        Random rng = new Random();
        int[] nums = new int[100];
        for (int i = 0; i < 100; i++) {
            int number = rng.nextInt(listRange) + 1;
            nums[i] = number;
        }

        for (int i = 0; i < nums.length - 1; i++){
            if (nums[i] > nums[i + 1]){
                int temp = nums[i];
                nums[i] = nums[i + 1];
                nums[i + 1] = temp;
                if (i > 0) i -= 2;
            }
        }

//        for (int i = 0; i < numList.size() - 1; i++){
//            if (numList.get(i) > numList.get(i + 1)){
//                int temp = numList.get(i);
//                numList.add(i, numList.get(i + 1));
//                numList.add(i + 1, temp);
//                if (i > 0) {
//                    i = i - 2;
//                }
//            }
//        }
        return nums;
    }

    @Benchmark
    public int[] bubbleSortNormal(){
        Random rng = new Random();
        int[] nums = new int[100];
        for (int i = 0; i < 100; i++) {
            int number = rng.nextInt(listRange) + 1;
            nums[i] = number;
        }

        for (int i = 0; i < nums.length; i++){
            for (int j = i + 1; j < nums.length; j++){
                if (nums[i] > nums[j]){
                    int temp = nums[i];
                    nums[i] = nums[j];
                    nums[j] = temp;
                }
            }
        }
        return nums;
    }

//    @Benchmark
//    public List<Integer> forLoop() {
//        List<Integer> secondNumList = new ArrayList<>();
//        for (int i = 0; i < 100000; i++) {
//            secondNumList.add((int) Math.round(Math.sqrt(numList.get(i).doubleValue() * 2.0)));
//        }
//        return secondNumList;
//    }
//
//    @Benchmark
//    public List<Integer> streamList() {
//        return numList.stream().map(num -> (int) Math.round(Math.sqrt(num.doubleValue() * 2.0))).collect(Collectors.toList());
//    }

//    @Benchmark
//    public List<Integer> enhancedForLoop() {
//        // enhanced for loop
//        List<Integer> fourthNumList = new ArrayList<>();
//        for (Integer numb : numList) {
//            fourthNumList.add(numb * 2);
//        }
//        return fourthNumList;
//    }
    
//    @Benchmark
//    public List<Integer> forEachLoop() {
//        List<Integer> fifthNumList = new ArrayList<>();
//        numList.forEach(
//                (x) -> {
//                    fifthNumList.add(x * 2);
//                }
//        );
//        return fifthNumList;
//    }
    
//    @Benchmark
//    public List<Integer> parallelStreamList() {
//        return numList.parallelStream().map(num -> (int) Math.round(Math.sqrt(num.doubleValue() * 2.0))).collect(Collectors.toList());
//    }
}
