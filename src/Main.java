import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class Main {
    private final static File dataSource = new File("file1.txt");
    private final static File results = new File("file2.txt");
    private final static File projectFiles = new File("file3.txt");

    public static void main(String[] args) throws Exception {
        FileReader fr = new FileReader(dataSource);
        Scanner sc = new Scanner(fr);

        if (!sc.hasNextLine())
            throw new Exception("Empty data source!");

        double sunDiameterMiles = getDiameterFromFile(sc);
        double earthDiameterMiles = getDiameterFromFile(sc);
        fr.close();
        sc.close();

        FileWriter fw = new FileWriter(results);

        double sunVolume = getVolume(sunDiameterMiles);
        System.out.println("Sun volume: " + sunVolume);
        fw.write("Sun volume: " + sunVolume + "\n");


        double earthVolume = getVolume(earthDiameterMiles);
        System.out.println("Earth volume: " + earthVolume);
        fw.write("Earth volume: " + earthVolume + "\n");

        double ratio = sunVolume / earthVolume;
        System.out.println("Ratio: " + ratio);
        fw.write("Ratio: " + ratio);
        fw.close();

        FileWriter finalFW = new FileWriter(projectFiles);

        String file1Content = fileToString(dataSource);
        String file2Content = fileToString(results);
        finalFW.write(file1Content + "\n");
        finalFW.write(file2Content);

        finalFW.close();
    }

    private static double getDiameterFromFile(@NotNull Scanner sc) {
        return Double.parseDouble(sc.nextLine());
    }

    private static double getVolume(double diameter) {
        return 4 * Math.PI * Math.pow(diameter / 2, 3) / 3;
    }

    private static String fileToString(File file) throws IOException {
        return String.join("\n", Files.readAllLines(file.toPath()));
    }
}