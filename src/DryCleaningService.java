import java.util.ArrayList;
import java.util.List;

public class DryCleaningService extends Service {
    private List<String> items;
    private List<Double> itemPrices;

    public DryCleaningService() {
        super("Dry Cleaning");
        items = new ArrayList<>();
        itemPrices = new ArrayList<>();
    }

    public void addItem(String item, double price) {
        items.add(item);
        itemPrices.add(price);
    }

    public void calculatePrice() {
        price = 0;
        for (double itemPrice : itemPrices) {
            price += itemPrice;
        }
        price *= 1.15; // Add 15% VAT
    }

    public List<String> listItems() {
        return items;
    }
}
