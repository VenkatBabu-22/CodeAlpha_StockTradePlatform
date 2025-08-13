import java.io.*;
import java.util.*;
public class StockTradingPlatform {

    static class Stock {
        String symbol;
        double price;

        Stock(String symbol, double price) {
            this.symbol = symbol;
            this.price = price;
        }

        @Override
        public String toString() {
            return symbol + " - $" + price;
        }
    }

    static class Holding {
        String symbol;
        int quantity;
        double buyPrice;

        Holding(String symbol, int quantity, double buyPrice) {
            this.symbol = symbol;
            this.quantity = quantity;
            this.buyPrice = buyPrice;
        }

        @Override
        public String toString() {
            return symbol + ": " + quantity + " shares @ $" + buyPrice;
        }
    }

    private static final List<Stock> market = new ArrayList<>();
    private static final Map<String, Holding> portfolio = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String FILE_NAME = "portfolio.txt";

    public static void main(String[] args) {
        loadPortfolio();
        initializeMarket();
        mainMenu();
        savePortfolio();
    }

    private static void initializeMarket() {
        market.add(new Stock("AAPL", 190.45));
        market.add(new Stock("GOOGL", 2780.55));
        market.add(new Stock("AMZN", 3500.25));
        market.add(new Stock("TSLA", 720.90));
        market.add(new Stock("MSFT", 310.10));
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n--- Stock Trading Platform ---");
            System.out.println("1. View Market");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewMarket();
                case 2 -> buyStock();
                case 3 -> sellStock();
                case 4 -> viewPortfolio();
                case 5 -> {
                    System.out.println("Thank you for using the platform.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewMarket() {
        System.out.println("\n--- Market Data ---");
        for (Stock stock : market) {
            System.out.println(stock);
        }
    }

    private static void buyStock() {
        System.out.print("Enter stock symbol to buy: ");
        String symbol = scanner.nextLine().toUpperCase();

        Stock selected = findStock(symbol);
        if (selected == null) {
            System.out.println("Stock not found.");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        Holding holding = portfolio.getOrDefault(symbol, new Holding(symbol, 0, selected.price));
        holding.quantity += quantity;
        holding.buyPrice = selected.price; // update to latest buy price
        portfolio.put(symbol, holding);

        System.out.println("Purchased " + quantity + " shares of " + symbol);
    }

    private static void sellStock() {
        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.nextLine().toUpperCase();

        if (!portfolio.containsKey(symbol)) {
            System.out.println("You don't own any shares of " + symbol);
            return;
        }

        Holding holding = portfolio.get(symbol);
        System.out.println("You own " + holding.quantity + " shares.");
        System.out.print("Enter quantity to sell: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        if (quantity <= 0 || quantity > holding.quantity) {
            System.out.println("Invalid quantity.");
            return;
        }

        holding.quantity -= quantity;
        if (holding.quantity == 0) {
            portfolio.remove(symbol);
        } else {
            portfolio.put(symbol, holding);
        }

        System.out.println("Sold " + quantity + " shares of " + symbol);
    }

    private static void viewPortfolio() {
        System.out.println("\n--- Your Portfolio ---");
        if (portfolio.isEmpty()) {
            System.out.println("No stocks owned.");
            return;
        }

        for (Holding holding : portfolio.values()) {
            Stock current = findStock(holding.symbol);
            double currentValue = current.price * holding.quantity;
            double profit = (current.price - holding.buyPrice) * holding.quantity;

            System.out.println(holding);
            System.out.printf("Current Price: $%.2f | Value: $%.2f | Profit/Loss: $%.2f\n",
                    current.price, currentValue, profit);
        }
    }

    private static Stock findStock(String symbol) {
        for (Stock stock : market) {
            if (stock.symbol.equalsIgnoreCase(symbol)) return stock;
        }
        return null;
    }

    private static void savePortfolio() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Holding h : portfolio.values()) {
                writer.write(h.symbol + "," + h.quantity + "," + h.buyPrice);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving portfolio.");
        }
    }

    private static void loadPortfolio() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String symbol = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);
                    portfolio.put(symbol, new Holding(symbol, quantity, price));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading portfolio.");
        }
}
}
