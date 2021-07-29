import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Sim {

    List<Patch> allPatches = new ArrayList<>();
    int tick = 1;

    /**
     * Initialize the matrix with patches and daisies
     */
    public void setup() {
        initializePatches();
        setNeighbors();
        seedRandomly(Color.WHITE, Params.START_WHITE_PERCENTAGE);
        seedRandomly(Color.BLACK, Params.START_BLACK_PERCENTAGE);
        setDaisiesWithRandomAge();
    }

    /**
     * Loops the plots indefinitely until the tick reaches the limit
     *
     * @throws IOException
     */
    public void go() throws IOException {

        while (tick < Params.ITERATION_LIMIT + 1) {
            if(Params.MODE.equals(Mode.RAMP_UP_RAMP_DOWN.toString())){
                changeLuminosity(tick);
            }

            allPatches.forEach(Patch::calculateTemperature);
            allPatches.forEach(Patch::diffuse);
            allPatches.forEach(patch -> {
                if (patch.getDaisy() != null) {
                    patch.getDaisy().checkSurvivability();
                }
            });

            if(Params.EXTENSION && tick % Params.INFECTION_CYCLE == 0){
                infectRandomly();
            }

            outputToCSV();
            outputToStdOut();
            tick++;
        }
    }

    /**
     * [Extension]:
     * Randomly infect a healthy daisy. If all daisies are infected, do nothing.
     */
    public void infectRandomly(){
        List<Patch> potentialInfectingPlaces =
                allPatches
                        .stream()
                        .filter(p ->
                                    p.getDaisy() != null &&
                                    !p.getDaisy().isInfected()
                                )
                        .collect(Collectors.toList());

        if(!potentialInfectingPlaces.isEmpty()){
            int index = new Random().nextInt(potentialInfectingPlaces.size());
            potentialInfectingPlaces.get(index).getDaisy().setInfected(true);
        }
    }

    /**
     * Change the solar luminosity according to time(tick)
     * @param tick the steps this program has run
     */
    private void changeLuminosity(int tick) {
        if(tick>= Params.RISE_START &&
                tick < Params.RISE_START + Params.RISE_TICKS){
            increaseLuminosity();
        }
        if(tick >= Params.DROP_START
                && tick < Params.DROP_START + Params.DROP_TICKS){
            decreaseLuminosity();
        }
    }

    /**
     * Decrease the solar luminosity according to the speed set in config
     */
    private void decreaseLuminosity() {
        double decreaseSpeed =
                (Params.SOLAR_LUMINOSITY_PEAK - Params.SOLAR_LUMINOSITY_END)
                    / Params.DROP_TICKS;
        Params.SOLAR_LUMINOSITY -= decreaseSpeed;
    }

    /**
     * Increase the solar luminosity according to the speed set in config
     */
    private void increaseLuminosity() {
        double increaseSpeed =
                (Params.SOLAR_LUMINOSITY_PEAK - Params.SOLAR_LUMINOSITY)
                        / Params.RISE_TICKS;
        Params.SOLAR_LUMINOSITY += increaseSpeed;
    }

    /**
     * Count the number of daisies in the world
     *
     * @return the number of daisies
     */
    public int calculateGlobalPopulation() {
        return (int) allPatches
                .stream()
                .filter(p -> p.getDaisy() != null)
                .count();
    }

    /**
     * Count the number of daisies of chosen color
     * @param color the color of daisies
     * @return the number of certain color daisy
     */
    public int calculatePopulation(Color color) {
        return (int) allPatches
                .stream()
                .filter(p ->
                            p.getDaisy() != null &&
                            p.getDaisy().getColor() == color
                        )
                .count();
    }

    public int calculateInfectedPopulation(Color color){
        return (int) allPatches
                .stream()
                .filter(p ->
                            p.getDaisy() != null &&
                            p.getDaisy().getColor() == color &&
                            p.getDaisy().isInfected()
                        )
                .count();
    }

    /**
     * Calculate the global temperature by adding up the temperatures
     * of each patch and divide by the number of patches
     *
     * @return the mean of all patches' temperature
     */
    public double calculateGlobalTemperature() {
        int totalTemperature = 0;
        for (Patch patch : allPatches) {
            totalTemperature += patch.getTemperature();
        }
        return totalTemperature / allPatches.size();
    }

    /**
     * Calculate the average of the sum of local temperature
     * for a specific color of daisies
     *
     * @return the average local temperature
     * for a particular type of daisies if they are not extinct
     * , otherwise return "NaN"
     */
    public double calculateLocalTemperatureAvg(Color color){
        int population = calculatePopulation(color);
        double localTempSum =
                allPatches
                        .stream()
                        .filter(p -> p.getDaisy() != null &&
                                     p.getDaisy().getColor() == color)
                        .mapToDouble(Patch::getTemperature).sum();

        // if the target color of daisies are not extinct:
        //      round the local temperature average up to 2 decimal
        // otherwise: NaN
        return population == 0
                ? Double.NaN
                :(double)Math.round(localTempSum/population * 100) / 100;
    }

    /**
     * Initialize all the patches
     */
    public void initializePatches() {
        for (int i = 0; i < Params.X_SIZE; i++) {
            for (int j = 0; j < Params.Y_SIZE; j++) {
                Patch patch = new Patch(i, j);
                allPatches.add(patch);
            }
        }
    }

    /**
     * Set the neighbors property for each patch
     */
    public void setNeighbors() {
        for (Patch patch : allPatches) {
            List<Patch> neighbors = allPatches.stream()
                    // Find all the patches in the matrix within 1 distance of the current patch
                    .filter(p -> Math.abs(p.getCoordinateX() - patch.getCoordinateX()) <= 1)
                    .filter(p -> Math.abs(p.getCoordinateY() - patch.getCoordinateY()) <= 1)
                    // Filter out the current patch itself
                    .filter(p -> p != patch)
                    .collect(Collectors.toList());
            patch.setNeighbors(neighbors);
        }
    }

    /**
     * Seed daisies of chosen color randomly on the matrix
     *
     * @param color      the color of daisies to be seeded
     * @param percentage the proportion of the daisies in the whole matrix
     */
    public void seedRandomly(Color color, double percentage) {
        // Find patches without daisies as the potential seeding place
        List<Patch> potentialSeedingPlaces =
                allPatches
                        .stream()
                        .filter(p -> p.getDaisy() == null)
                        .collect(Collectors.toList());

        int matrixSize = Params.X_SIZE * Params.Y_SIZE;
        // Calculate the amount of daisies to be seeded
        int size = (int) Math.round(percentage * matrixSize);

        List<Patch> seedingPlaces = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            // Select one seeding place randomly
            int index = new Random().nextInt(potentialSeedingPlaces.size());
            seedingPlaces.add(potentialSeedingPlaces.get(index));
            // Remove the added seeding place from the wait list
            potentialSeedingPlaces.remove(index);
        }

        seedingPlaces.forEach(patch -> patch.setDaisy(Daisy.sproutDaisy(color,patch)));
    }

    /**
     * Randomly set the age of all daisies in the matrix
     */
    public void setDaisiesWithRandomAge() {
        allPatches.stream().filter(p -> p.getDaisy() != null)
                .forEach(p ->
                        p.getDaisy()
                         .setAge(new Random().nextInt(Params.MAX_AGE))
                );
    }

    /**
     * Write data to a csv file
     * @throws IOException if data outputting fails
     */
    public void outputToCSV() throws IOException {
        // Record the result in CSV
        String[] csvData = {
                String.valueOf(tick),
                String.valueOf(calculatePopulation(Color.WHITE)),
                String.valueOf(calculateLocalTemperatureAvg(Color.WHITE)),
                String.valueOf(calculateInfectedPopulation(Color.WHITE)),
                String.valueOf(calculatePopulation(Color.BLACK)),
                String.valueOf(calculateLocalTemperatureAvg(Color.BLACK)),
                String.valueOf(calculateInfectedPopulation(Color.BLACK)),
                String.valueOf(calculateGlobalPopulation()),
                String.valueOf(calculateGlobalTemperature())};
        WriterCSV.writeToCSV(csvData);
    }

    /**
     * Print record for each tick to standard output
     */
    public void outputToStdOut(){
        System.out.println("Tick: " + tick);
        System.out.println("White_Daisies: " + calculatePopulation(Color.WHITE));
        System.out.println("White_Daisies_Local_Temperature_Average: "
                + calculateLocalTemperatureAvg(Color.WHITE));
        System.out.println("Infected_White_Daisies: "
                + calculateInfectedPopulation(Color.WHITE));
        System.out.println("Black_Daisies: " + calculatePopulation(Color.BLACK));
        System.out.println("Black_Daisies_Local_Temperature_Average: "
                + calculateLocalTemperatureAvg(Color.BLACK));
        System.out.println("Infected_Black_Daisies: "
                + calculateInfectedPopulation(Color.BLACK));
        System.out.println("Global_Population: " + calculateGlobalPopulation());
        System.out.println("Global_Temperature: " + calculateGlobalTemperature());
    }
}
