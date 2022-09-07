import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    private final static File dataSource = new File("file1.txt");
    private final static File results = new File("file2.txt");
    private final static File projectFiles = new File("file3.txt");

    public static void main(String[] args) throws Exception {
        FileReader fr = new FileReader(dataSource);
        Scanner sc = new Scanner(fr);

        if (!sc.hasNextLine())
            throw new Exception("Empty file");

        double sunDiameterMiles = getDiameter(sc);
        double earthDiameterMiles = getDiameter(sc);

        long sunVolume = getVolume(sunDiameterMiles);
        long earthVolume = getVolume(earthDiameterMiles);

        System.out.println("Sun volume: " + sunVolume);
        System.out.println("Earth volume: " + earthVolume);
        System.out.println("Ratio: " + (sunVolume / earthVolume));
    }

    private static double getDiameter(@NotNull Scanner sc) {
        return Double.parseDouble(sc.nextLine());
    }

    private static long getVolume(double diameter) {
        return (long) (4 * Math.PI * Math.pow(diameter / 2, 3) / 3);
    }
}