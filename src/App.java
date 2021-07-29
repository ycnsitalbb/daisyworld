public class App {
    public static void main(String[] args) throws Exception {
            Params.configParams();
            WriterCSV.initCSV();
            Sim sim = new Sim();
            sim.setup();
            sim.go();
            WriterCSV.flushAndClose();
    }
}
