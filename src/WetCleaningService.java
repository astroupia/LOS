public class WetCleaningService extends Service {
    private double weight; // in kg
    private double pricePerKg;

    public WetCleaningService(double pricePerKg) {
        super("Wet Cleaning");
        this.pricePerKg = pricePerKg;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public void calculatePrice() {
        price = weight * pricePerKg * 1.15; // Add 15% VAT
    }
}
