abstract class Service {
    protected String serviceType;
    protected double price;

    public Service(String serviceType) {
        this.serviceType = serviceType;
    }

    public abstract void calculatePrice();

    public String getServiceType() {
        return serviceType;
    }

    public double getPrice() {
        return price;
    }
}
