import java.util.List;

public class Patch {
    private final int coordinateX;
    private final int coordinateY;
    private double temperature = 0;
    private List<Patch> neighbors;
    private Daisy daisy = null;

    /**
     * The constructor of Patch
     * @param coordinateX x
     * @param coordinateY y
     */
    public Patch(int coordinateX, int coordinateY) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    /**
     * Diffuse 50% of the current temperature to its eight neighbors
     * Each neighbor can get 1/8 of the temperature diffused
     * If the patch has less than 8 neighbors, it will keep the leftover shares
     */
    public void diffuse(){
        double share = this.temperature * 0.5 / 8;
        neighbors.forEach(p -> p.temperature += share);
        if(neighbors.size()==8){
            this.temperature *= 0.5;
        }else{
            this.temperature = 0.5 * this.temperature
                    + ( 8 - neighbors.size()) * share;
        }
    }

    /**
     * Calculate the temperature of a patch
     */
    void calculateTemperature(){

        double absorbedLuminosity = calcAbsorbedLuminosity();

        double localHeating = calcLocalHeating(absorbedLuminosity);

        this.temperature = (this.temperature + localHeating) / 2;
    }

    /**
     * Calculate the local heating
     * @param absorbedLuminosity the absorbed luminosity calculated previously
     * @return the local heating data for the calculation of temperature
     */
    private double calcLocalHeating(double absorbedLuminosity) {
        if(absorbedLuminosity > 0){
            // https://stackoverflow.com/questions/2568142/how-to-use-ln-in-java
            return  72 * Math.log(absorbedLuminosity) + 80;
        }else{
            return 80;
        }
    }

    /**
     * Calculate the absorbed luminosity
     * @return absorbed luminosity
     */
    private double calcAbsorbedLuminosity() {
        if(this.daisy == null){
            return (1 - Params.ALBEDO_OF_SURFACE) * Params.SOLAR_LUMINOSITY;
        }else{
            return (1 - this.daisy.getAlbedo()) * Params.SOLAR_LUMINOSITY;
        }
    }


    public int getCoordinateX() {
        return coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public double getTemperature() {
        return temperature;
    }

    public Daisy getDaisy() {
        return daisy;
    }

    public void setDaisy(Daisy daisy) {
        this.daisy = daisy;
    }

    public List<Patch> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Patch> neighbors) {
        this.neighbors = neighbors;
    }
}
