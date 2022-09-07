import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
//    12. На склад с некоторой фирмы подъезжают грузовики для погрузки груза. Всего надо вывести 145 т. груза.
//    У склада только одни ворота. Всего у фирмы 3 грузовика. У каждого грузовика своя
//    грузовместимость (1,5 т., 3,4 т. и 5.1 т., соответственно), также каждый грузовик характеризуется своим временем,
//    затрачиваемым на доставку груза со склада (30 мин., 45 мин., 40 мин.). Отгрузка из ворот склада осуществляется так,
//    что пока один грузовик не загружен полностью, не переходят к погрузке следующего грузовика.
//    Время погрузки одной тонны груза составляет 3 мин. Приложение должно выводить общее время, за которое весь груз
//    будет вывезен со склада и количество рейсов, которое сделает каждый грузовик.

    private final static int FINAL_MASS = 145;  // tons
    private final static int TON_DELAY = 180;   // seconds

    private static final ArrayList<TruckCallable> tasks = new ArrayList<>(3);

    private static final AtomicReference<Float> currentMass = new AtomicReference<>(0.0f); // tons
    private static final AtomicLong time = new AtomicLong(0);

    private static final ExecutorService pool = Executors.newFixedThreadPool(3);


    public static void main(String[] args) throws InterruptedException {
        tasks.add(new TruckCallable(new Truck(1.5, 1800)));
        tasks.add(new TruckCallable(new Truck(3.4, 2700)));
        tasks.add(new TruckCallable(new Truck(5.1, 2400)));

        pool.invokeAll(tasks);

        System.out.println("Work done!");
        System.out.println(currentMass.get());
        System.out.println(time.get());
    }

    static class Truck {
        public final double loadCapacity;   // tons
        public final int deliveryTime;  // seconds

        public Truck(double loadCapacity, int deliveryTime) {
            this.loadCapacity = loadCapacity;
            this.deliveryTime = deliveryTime;
        }
    }

    static class TruckCallable implements Callable<Double> {

        private final Truck truck;

        public TruckCallable(Truck truck) {
            this.truck = truck;
        }

        @Override
        public Double call() throws Exception {
            while (true) {
                float massToSet = currentMass.get() + (float) truck.loadCapacity;

                if (massToSet > FINAL_MASS) massToSet = FINAL_MASS;

                float massToAdd = massToSet - currentMass.get();
//                System.out.println(massToAdd + "   " + currentMass.get());
                long timeToUnload = (long) (TON_DELAY * massToAdd);

                Thread.sleep(timeToUnload / 10);

                currentMass.set(massToSet);
                time.addAndGet(timeToUnload);

                System.out.println(Thread.currentThread().getId() + " thread: "
                        + time.get() + " sec. " + "mass = " + currentMass.get());

                if (massToSet >= FINAL_MASS) {
                    pool.shutdownNow();
                    break;
                }

                Thread.sleep(truck.deliveryTime / 10);
            }

            return null;
        }
    }
}
