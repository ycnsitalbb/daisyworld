import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class Daisy {
    private int age = 0;
    private boolean isInfected;
    private final double albedo;
    private final Color color;
    private final Patch patch;

    /**
     * The constructor of Daisy
     * @param age age of daisy
     * @param albedo the reflection rate
     * @param color color of daisy
     * @param patch the patch the daisy resides in
     */
    public Daisy(int age, double albedo, Color color, Patch patch) {
        this.age = age;
        this.albedo = albedo;
        this.color = color;
        this.patch = patch;
        isInfected = false;
    }

    /**
     * Check if the daisy reaches its max age,
     * or dies from infection if the extension mode is on.<br><br>
     *
     * If either is true detach the daisy from the patch it resides as its death
     * <br><br>
     * Else infect the surrounding neighbours if the extension mode is on,
     *      followed by incrementing its age by 1 and
     *      sprouting a new daisy with a probability.
     */
    public void checkSurvivability() {
        if (age >= Params.MAX_AGE
                || (Params.EXTENSION && isInfected && !survivedInfection())) {
            patch.setDaisy(null);
        } else {

            if (Params.EXTENSION && isInfected) {
                infectNeighbours();
            }
            this.age++;
            sprout();
        }
    }

    /**
     * [Extension]:
     * Check if the infected daisy can survive infection if it didn't recover.
     * @return false if the lethal rate is reached, otherwise true.
     */
    public boolean survivedInfection(){
        if(!recoveredFromInfection()){
            double survivalPossibility = new Random().nextDouble();
            return survivalPossibility >= Params.DEATH_RATE;
        }
        return true;
    }

    /**
     * [Extension]:
     * Check if the infected daisy can recover depending on the recovery rate
     * @return true if the infected daisy recovers, otherwise false.
     */
    public boolean recoveredFromInfection(){
            double recoveryPossibility = new Random().nextDouble();
            if(recoveryPossibility <= Params.RECOVERY_RATE){
                isInfected = false;
                return true;
            }
            return false;
    }

    /**
     * [Extension]:
     * Infect healthy neighbours of daisies based on infectious threshold
     */
    public void infectNeighbours(){
        List<Patch> potentialInfectingPlaces =
                patch.getNeighbors()
                        .stream()
                        .filter(p -> p.getDaisy() != null)
                        .filter(p -> !p.getDaisy().isInfected())
                        .collect(Collectors.toList());

        // if there are healthy daisies nearby,
        if(!potentialInfectingPlaces.isEmpty()){

            // calculate the probability of being infected for every neighbour.
            // if the infectious threshold is reached, it will be infected.
            for(Patch vulnerablePatch : potentialInfectingPlaces){
                double infectiousPossibility = new Random().nextDouble();
                if(infectiousPossibility <= Params.INFECTION_RATE){
                    vulnerablePatch.getDaisy().setInfected(true);
                }
            }
        }
    }

    /**
     * Sprout a new daisy with the same color as the current one.
     * The sprouting place is one of its neighbors that has no daisy inside.
     */
    public void sprout() {
        double temperature = patch.getTemperature();

        // Calculate the threshold of the sprouting possibility
        // The threshold is determined by the temperature:
        // temperature too low or too high -> threshold < 0
        // temperature = 22.5 -> threshold = 1 (MAX)
        double sproutThreshold =
                0.1457 * temperature - 0.0032 * temperature * temperature - 0.6443;

        // Generate a possibility from 0 to 1
        double sproutPossibility = new Random().nextDouble();

        // If the possibility is within the threshold, sprout
        if (sproutPossibility < sproutThreshold) {
            // Find the neighbors without daisies inside
            List<Patch> potentialSproutPlaces =
                    patch.getNeighbors()
                            .stream()
                            .filter(p -> p.getDaisy() == null)
                            .collect(Collectors.toList());

            // If there is a seeding place available, start sprouting
            if(!potentialSproutPlaces.isEmpty()){
                // Randomly select one qualified neighbor as the sprouting place
                int placeIndex = new Random().nextInt(potentialSproutPlaces.size());
                Patch sproutPlace = potentialSproutPlaces.get(placeIndex);
                sproutPlace.setDaisy(sproutDaisy(color, sproutPlace));
            }
        }
    }

    /**
     * Sprout a chosen color of daisy
     * @param color the color of the daisy
     * @param patch the patch this daisy resides in
     * @return an instance of Daisy
     */
    public static Daisy sproutDaisy(Color color, Patch patch){
        if(color == Color.BLACK){
            return new Daisy(0, Params.ALBEDO_OF_BLACKS, Color.BLACK, patch);
        }else{
            return new Daisy(0, Params.ALBEDO_OF_WHITES, Color.WHITE, patch);
        }
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public double getAlbedo() {
        return albedo;
    }

    public Color getColor() {
        return color;
    }

    public Patch getPatch() {
        return patch;
    }

    public boolean isInfected() {
        return isInfected;
    }

    public void setInfected(boolean infected) {
        isInfected = infected;
    }
}
