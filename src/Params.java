import java.io.FileReader;
import java.util.Arrays;
import java.util.Properties;

public class Params {

    public static final int MAX_AGE = 25;

    public static final int X_SIZE = 29;

    public static final int Y_SIZE = 29;

    public static final String FILE_PATH = "src/config.properties";

    public static double START_WHITE_PERCENTAGE = 0.2;

    public static double START_BLACK_PERCENTAGE = 0.2;

    public static double ALBEDO_OF_WHITES = 0.75;

    public static double ALBEDO_OF_BLACKS = 0.75;

    public static double SOLAR_LUMINOSITY = 0.8;

    public static double ALBEDO_OF_SURFACE;

    public static int ITERATION_LIMIT;

    public static String MODE;

    public static int RISE_START;
    public static int RISE_TICKS;
    public static int DROP_START;
    public static int DROP_TICKS;
    public static double SOLAR_LUMINOSITY_PEAK;
    public static double SOLAR_LUMINOSITY_END;

    public static boolean EXTENSION;
    public static int INFECTION_CYCLE;
    public static double RECOVERY_RATE;
    public static double DEATH_RATE;
    public static double INFECTION_RATE;

    /**
     * Read initial Daisyworld configuration from "config.properties" file
     * @throws Exception if any parsed value is out of bound
     */
    public static void configParams() throws Exception {

        Properties daisyWorldProperties = new Properties();
        try (FileReader inStream = new FileReader(FILE_PATH)) {
            daisyWorldProperties.load(inStream);
        }

        // Model Parameters
        START_WHITE_PERCENTAGE = Double.parseDouble
                (daisyWorldProperties.getProperty("START_WHITE_PERCENTAGE"));
        START_BLACK_PERCENTAGE = Double.parseDouble
                (daisyWorldProperties.getProperty("START_BLACK_PERCENTAGE"));

        ALBEDO_OF_WHITES = Double.parseDouble
                (daisyWorldProperties.getProperty("ALBEDO_OF_WHITES"));
        ALBEDO_OF_BLACKS = Double.parseDouble
                (daisyWorldProperties.getProperty("ALBEDO_OF_BLACKS"));

        SOLAR_LUMINOSITY = Double.parseDouble
                (daisyWorldProperties.getProperty("SOLAR_LUMINOSITY"));
        ALBEDO_OF_SURFACE = Double.parseDouble
                (daisyWorldProperties.getProperty("ALBEDO_OF_SURFACE"));

        ITERATION_LIMIT = Integer.parseInt
                (daisyWorldProperties.getProperty("ITERATION_LIMIT"));

        // Scenarios
        MODE = daisyWorldProperties.getProperty("MODE");

        RISE_START = Integer.parseInt
                (daisyWorldProperties.getProperty("RISE_START"));
        RISE_TICKS = Integer.parseInt
                (daisyWorldProperties.getProperty("RISE_TICKS"));
        DROP_START = Integer.parseInt
                (daisyWorldProperties.getProperty("DROP_START"));
        DROP_TICKS = Integer.parseInt
                (daisyWorldProperties.getProperty("DROP_TICKS"));
        SOLAR_LUMINOSITY_PEAK = Double.parseDouble
                (daisyWorldProperties.getProperty("SOLAR_LUMINOSITY_PEAK"));
        SOLAR_LUMINOSITY_END = Double.parseDouble
                (daisyWorldProperties.getProperty("SOLAR_LUMINOSITY_END"));

        // Extension
        EXTENSION = Boolean.parseBoolean
                (daisyWorldProperties.getProperty("EXTENSION"));
        INFECTION_CYCLE = Integer.parseInt
                (daisyWorldProperties.getProperty("INFECTION_CYCLE"));
        RECOVERY_RATE = Double.parseDouble
                (daisyWorldProperties.getProperty("RECOVERY_RATE"));
        DEATH_RATE = Double.parseDouble
                (daisyWorldProperties.getProperty("DEATH_RATE"));
        INFECTION_RATE = Double.parseDouble
                (daisyWorldProperties.getProperty("INFECTION_RATE"));

        checkParamsValidity();
    }

    /**
     * Check the validity of configuration parameters
     * @throws Exception if any value is out of bound
     */
    public static void checkParamsValidity() throws Exception {
        checkDaisyPercentage(START_WHITE_PERCENTAGE, Color.WHITE);
        checkDaisyPercentage(START_BLACK_PERCENTAGE, Color.BLACK);

        checkAlbedoOfDaisy(ALBEDO_OF_WHITES, Color.WHITE);
        checkAlbedoOfDaisy(ALBEDO_OF_BLACKS, Color.BLACK);


        if(SOLAR_LUMINOSITY < 0.001 || SOLAR_LUMINOSITY > 3){
            throw new Exception("Solar Luminosity should be " +
                    "between 0.001 and 3 inclusive" );
        }

        if(ALBEDO_OF_SURFACE < 0 || ALBEDO_OF_SURFACE > 1){
            throw new Exception("Albedo of Surface should be " +
                    "between 0 and 1 inclusive" );
        }

        if(!MODE.equals(Mode.CONSTANT.toString())
                && !MODE.equals(Mode.RAMP_UP_RAMP_DOWN.toString())){
            throw new Exception("Mode should be " +
                    "either CONSTANT or RAMP_UP_RAMP_DOWN" );
        }

        // extension
        if(INFECTION_CYCLE <= 0 || INFECTION_CYCLE >= ITERATION_LIMIT){
            throw new Exception("Infection cycle should be " +
                    "within the iteration limit");
        }
        checkExtensionParams(new Double[]
                {RECOVERY_RATE, DEATH_RATE, INFECTION_RATE});

    }

    /**
     * Check that the value of the start percentage of daisies
     * with a specific color is within the range of [0, 0.5]
     * @param percentage the initial percentage of daisies
     * @param color the color of daisies
     * @throws Exception if the value is out of bound
     */
    public static void checkDaisyPercentage(double percentage, Color color)
                                                            throws Exception {
        if(percentage < 0 || percentage > 0.5){
            throw new Exception("The initial percentage of "
                    + color.toString().toLowerCase()
                    + " daisies should be between 0 and 0.5 inclusive" );
        }
    }

    /**
     * Check that the albedo value of one typed daisies is
     * within the range of [0, 0.99]
     * @param albedo the albedo of one typed daisies
     * @param color the color of daisies
     * @throws Exception if the value is out of bound
     */
    public static void checkAlbedoOfDaisy(double albedo, Color color)
                                                            throws Exception {
        if(albedo < 0 || albedo > 0.99){
            throw new Exception("The albedo of "
                    + color.toString().toLowerCase()
                    + " daisies should be between 0 and 0.99 inclusive" );
        }
    }

    /**
     * Check that every probability of extension params is within [0, 1]
     * @param probabilities a list of probabilities of extension params
     * @throws Exception if any rate is not within [0, 1]
     */
    public static void checkExtensionParams(Double[] probabilities)
                                                            throws Exception {
        if(!Arrays.stream(probabilities).allMatch(i -> i >= 0 && i <= 1)){
            throw new Exception("The probabilities of extension parameters "
                    + "should be between 0 and 1 inclusive" );
        }
    }
}
