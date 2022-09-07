import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BinaryOperator;
import java.util.function.ToLongFunction;

public class Main {
    private final static int FINAL_MASS = 145000;  // kg
    private final static int TON_DELAY = 180;   // seconds

    private static final AtomicInteger currentMass = new AtomicInteger(0);   // kg

    private static final ExecutorService pool = Executors.newFixedThreadPool(3);
    private static List<Future<Double>> futures;

    private static final ArrayList<TruckCallable> tasks = new ArrayList<>(3);

    public static void main(String[] args) throws InterruptedException {
        tasks.add(new TruckCallable(new Truck(0, 1500, 1800)));
        tasks.add(new TruckCallable(new Truck(1, 3400, 2700)));
        tasks.add(new TruckCallable(new Truck(2, 5100, 2400)));

        futures = pool.invokeAll(tasks);

        System.out.println("Work done!");
        System.out.println("Total time: " + tasks.stream()
                .mapToLong(value -> value.totalTimeSpend.get())
                .reduce(Long::sum)
                .orElse(0) / 60
        );
        System.out.println("Total tons: " + currentMass.get() / 1000);
        System.out.println(tasks);
    }

    static class Truck {
        public final long id;
        public final int loadCapacity;   // kg
        public final int deliveryTime;  // seconds

        public Truck(long id, int loadCapacity, int deliveryTime) {
            this.id = id;
            this.loadCapacity = loadCapacity;
            this.deliveryTime = deliveryTime;
        }

        @Override
        public String toString() {
            return "Truck{" +
                    "id=" + id +
                    '}';
        }
    }

    static class TruckCallable implements Callable<Double> {

        private final Truck truck;

        public final AtomicLong totalTimeSpend = new AtomicLong(0);  // min
        public final AtomicInteger totalRides = new AtomicInteger(0);

        public TruckCallable(Truck truck) {
            this.truck = truck;
        }

        @Override
        public Double call() throws Exception {
            while (true) {
                int massToSet = currentMass.get() + truck.loadCapacity;

                if (massToSet > FINAL_MASS) massToSet = FINAL_MASS;

                int massToAdd = massToSet - currentMass.get();
                long timeToUnload = TON_DELAY * massToAdd / 1000;

                Thread.sleep(timeToUnload / 100);

                currentMass.set(massToSet);
                totalTimeSpend.addAndGet(timeToUnload + truck.deliveryTime);

                System.out.println(Thread.currentThread().getId() + " Thread " + truck.id + " truck worked: "
                        + totalTimeSpend.get() + " total sec. " + "total mass = " + currentMass);

                if (massToSet >= FINAL_MASS) {
                    pool.shutdownNow();
                    futures.forEach((it) -> it.cancel(true));
                    break;
                }

                Thread.sleep(truck.deliveryTime / 100);
                totalRides.incrementAndGet();
            }

            return null;
        }

        @Override
        public String toString() {
            return "TruckCallable{" +
                    "truck=" + truck +
                    ", totalTimeSpend=" + totalTimeSpend +
                    ", totalRides=" + totalRides +
                    '}';
        }
    }
}