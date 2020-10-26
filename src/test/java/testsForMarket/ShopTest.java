package testsForMarket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import entities.Item;
import entities.Order;
import entities.User;
import market.Shop;
import market.Status;

public class ShopTest {
    private Map<Item, Long> goods = new HashMap<Item, Long>();
    Item validItem = new Item(1L, "Kettle", 4.99d);
    User user = new User("John", 200d);
    
    @AfterMethod
    public void cleanup() {
        goods.clear();
    }

    @Test(priority = 1)
    public void shouldSuccessfullyMakePurchase() {
        goods.put(validItem, 1L);
        Shop shop = new Shop(getItemsQuantity());
        Order order = new Order(user, Arrays.asList(validItem));
        Status status = shop.makePurchase(order);

        assert status == Status.OK :
                "Incorrect status after purchase. Expected: " + Status.OK + ", actual: " + status;
    }

    @Test(priority = 1)
    public void shouldNotPurchaseNonExistingItem() {
        goods.put(validItem, 1L);
        Shop shop = new Shop(getItemsQuantity());
        Item invalidItem = new Item(2L, "Snowboard", 150d);
        Order order = new Order(user, Arrays.asList(validItem, invalidItem));
        Status status = shop.makePurchase(order);

        assert status == Status.NON_EXISTING_ITEM :
                "Incorrect status after purchase. Expected: " + Status.NON_EXISTING_ITEM + ", actual: " + status;
    }

    @Test(priority = 1)
    public void shouldNotPurchaseLowBalance() {
        goods.put(validItem, 1L);
        Shop shop = new Shop(getItemsQuantity());
        User user = new User("John", 2d);
        Order order = new Order(user, Arrays.asList(validItem));
        Status status = shop.makePurchase(order);

        assert status == Status.USER_HAS_LOW_BALANCE :
                "Incorrect status after purchase. Expected: " + Status.USER_HAS_LOW_BALANCE + ", actual: " + status;
    }

    @Test(priority = 1)
    public void shouldNotPurchaseItemsLowStock() {
        goods.put(validItem, 1L);
        Shop shop = new Shop(getItemsQuantity());
        Order order = new Order(user, Arrays.asList(validItem, validItem));
        Status status = shop.makePurchase(order);

        assert status == Status.NOT_ENOUGH_ITEMS_IN_THE_SHOP :
                "Incorrect status after purchase. Expected: " + Status.NOT_ENOUGH_ITEMS_IN_THE_SHOP + ", actual: " + status;
    }
    
    @Test(priority = 2)
    public void shouldCorrectlyDecreaseBalance() {
        goods.put(validItem, 2L);
        Shop shop = new Shop(getItemsQuantity());

        double userInitialBalance = 200d;
        User user = new User("John", userInitialBalance);

        List<Item> itemsInOrder = Arrays.asList(validItem, validItem);
        Order order = new Order(user, itemsInOrder);

        double itemsTotalPrice = validItem.getCost() * 2;
        double expectedUserBalance = roundAvoid(userInitialBalance - itemsTotalPrice, 2);

        shop.makePurchase(order);

        assert user.getBalance() == expectedUserBalance :
                "User has insufficient balance. \n Initial user balance: " + userInitialBalance +
                        "\n User current balance: " + user.getBalance() + "\n Items cost: " + itemsTotalPrice;
    }

    @Test(priority = 2)
    public void shouldNotDecreaseBalanceInsuffitientBalance() {
        goods.put(validItem, 1L);
        Shop shop = new Shop(getItemsQuantity());

        double userInitialBalance = 2d;
        User user = new User("John", userInitialBalance);

        List<Item> itemsInOrder = Arrays.asList(validItem, validItem);
        Order order = new Order(user, itemsInOrder);

        shop.makePurchase(order);

        assert user.getBalance() == userInitialBalance :
                "User balance should not be decreased \n Initial user balance: " + userInitialBalance +
                        "\n User current balance: " + user.getBalance() + "\n Items cost: " + validItem.getCost();
    }

    @Test(priority = 2)
    public void shouldNotDecreaseBalanceNonExistingItem() {
        goods.put(validItem, 1L);
        Shop shop = new Shop(getItemsQuantity());

        Item invalidItem = new Item(2L, "Snowboard", 150d);
        double userInitialBalance = 200d;
        User user = new User("John", userInitialBalance);

        List<Item> itemsInOrder = Arrays.asList(validItem, invalidItem);
        Order order = new Order(user, itemsInOrder);

        shop.makePurchase(order);

        assert user.getBalance() == userInitialBalance :
                "User balance should not be decreased \n Initial user balance: " + userInitialBalance +
                        "\n User current balance: " + user.getBalance() + "\n Items cost: " + validItem.getCost();
    }

    @Test(priority = 2)
    public void shouldNotDecreaseBalanceItemLowStock() {
        goods.put(validItem, 1L);
        Shop shop = new Shop(getItemsQuantity());

        double userInitialBalance = 200d;
        User user = new User("John", userInitialBalance);

        List<Item> itemsInOrder = Arrays.asList(validItem, validItem);
        Order order = new Order(user, itemsInOrder);

        shop.makePurchase(order);

        assert user.getBalance() == userInitialBalance :
                "User balance should not be decreased \n Initial user balance: " + userInitialBalance +
                        "\n User current balance: " + user.getBalance() + "\n Items cost: " + validItem.getCost();
    }

    
    private Map<Long, Long> getItemsQuantity() {
        return goods.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
    }

    private double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
