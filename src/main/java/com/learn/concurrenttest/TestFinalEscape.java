package com.learn.concurrenttest;

import java.util.concurrent.TimeUnit;

public class TestFinalEscape {
    public static class EscapeFinal {
        final int i;
        static EscapeFinal obj;


        public EscapeFinal() {
            obj = this;
            i = 1;
        }

        public static void writer() {
            new EscapeFinal();
        }

        public static void reader() {
            if (obj != null) {
                System.out.println(obj.i);
            }
        }

        public static void reset() {
            obj = null;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (;;) {
//            ExcapeFinal obj = new ExcapeFinal();
            Thread a = new Thread(EscapeFinal::writer,"A");
            Thread b = new Thread(EscapeFinal::reader, "B");
            a.start();
            b.start();

            a.join();
            b.join();

            EscapeFinal.reset();
//            TimeUnit.MILLISECONDS.sleep(200);
        }
    }
}
