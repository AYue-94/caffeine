package com.github.benmanes.caffeine.cache;


public class MpscTest {
    public static void main(String[] args) {
        MpscGrowableArrayQueue<Integer> q = new MpscGrowableArrayQueue<>(4, 8);

        for (int i = 0; i < 10; i++) {
            while (q.offer(1)) {

            }
            System.out.println(q.size());
            while (q.poll() != null) {

            }
            System.out.println(q.size());
        }
    }
}
