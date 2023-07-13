package src;
import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.io.FileWriter;
import java.io.IOException;

import static org.opencv.core.Core.NATIVE_LIBRARY_NAME;

public class Main {
    private static String pixelToSymbol(int pixel_value, int b_idx) {
        String CHARS;
        if (b_idx == 1) {
            CHARS = "       .,_-=+*π!?&#¥%$@";
        } else if (b_idx == 2) {
            CHARS = " .,_-=+*±π!?∂ß&#¥%$@";
        } else {
            CHARS = "     .,_-=+*±π!?∂ß&#¥%$@";
        }

        int idx = pixel_value * CHARS.length() / 256;
        // original data is for 256 color,
        // so if you divide it by 256, you will get the position ratio in the symbol string
        // change the order, so we can get the integer idx.ß
        return CHARS.substring(idx, idx + 1);
    }
    public static void main(String[] args) throws IOException {
        System.out.println("\n\n======================================================");
        System.loadLibrary(NATIVE_LIBRARY_NAME);
        System.out.println("Loaded OpenCV version "+ Core.VERSION);
        Imgcodecs imageCodecs = new Imgcodecs();
        String path = "";
        String[] balanced = new String[]{"Normal", "Light-focus", "Dark-focus"};
        int balanceIdx = 0;
        if (args.length == 0) {
            System.out.println("Please input the Image direction.");
            System.out.println("======================================================\n\n");
            return;
        } else if (args.length == 1) {
            System.out.println("Converting the Image: " + args[0]);
            path = args[0];
        } else if (args.length == 2) {
            System.out.println("Converting the Image: " + args[0]);
            path = args[0];
            System.out.println("Adjusting the balance to: " + balanced[Integer.valueOf(args[1])]);
            balanceIdx = Integer.valueOf(args[1]);
        }

        System.out.println("======================================================\n\n");
        Mat matrix = imageCodecs.imread(path);

        int width = 150, height;
        height = (int) ((width * matrix.height() / matrix.width()) * 0.42);
        // 0.42 -> adjust the ratio of each symbol width and height because symbol has height > width.

        // convert the original matrix to gray
        Mat Gray_matrix = new Mat(matrix.height(), matrix.width(), CvType.CV_32F);
        Imgproc.cvtColor(matrix, Gray_matrix, Imgproc.COLOR_RGB2GRAY);

        Mat resize_matrix = new Mat();
        Imgproc.resize( Gray_matrix, resize_matrix, new Size(width, height), 0, 0, Imgproc.INTER_LINEAR);

        String symbol_frame = "";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                symbol_frame += pixelToSymbol((int) resize_matrix.get(i, j)[0], balanceIdx);
            }
            symbol_frame += "\n";
        }

        System.out.println(symbol_frame);

        // write out to a txt file
        FileWriter myObj = new FileWriter(args[0].split("\\.")[0] + "_converted.txt");
        myObj.write(symbol_frame);
        myObj.close();
        System.out.println("Finish writing file to: " + args[0].split("\\.")[0] + "_converted.txt");
    }
}
