import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * WriterCSV contains static helper methods for outputting a csv file
 */
public class WriterCSV {

    public static File daisyWorldCSV;
    public static BufferedWriter bw;

    /**
     * Initialize the csv file and its header
     *
     * @throws IOException if file initialization fails
     */
    public static void initCSV() throws IOException {
        if (Params.EXTENSION) {
            daisyWorldCSV = new File("daisyworld-ext-"+Params.MODE+".csv");
        }
        else {
            daisyWorldCSV = new File("daisyworld-"+Params.MODE+".csv");
        }
        
        bw = new BufferedWriter(new FileWriter(daisyWorldCSV, false));
        bw.write("tick" + "," +
                "number_of_whites" + "," +
                "local_temp_avg_whites" + "," +
                "infected_whites" + "," +
                "number_of_blacks" + "," +
                "local_temp_avg_blacks" + "," +
                "infected_blacks" + "," +
                "global_population" + "," +
                "global_temperature"
        );
        bw.newLine();
    }

    /**
     * Write values for each tick to "daisyworld.csv"
     *
     * @throws IOException if an I/O error occurs
     */
    public static void writeToCSV(String[] data) throws IOException {
        bw.write(Stream.of(data).collect(Collectors.joining(",")));
        bw.newLine();
    }

    /**
     * Flush the stream and close the stream
     */
    public static void flushAndClose() throws IOException {
        bw.flush();
        bw.close();
    }
}