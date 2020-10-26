package market;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import entities.Item;
import entities.Order;
import entities.User;

public class Shop {

    /**
     * Field describes available items in stock.
     * Model: [itemId : itemsQuantity]
     */
    private Map<Long, Long> stock;

    public Shop(Map<Long, Long> stock) {
        this.stock = stock;
    }

    public Status makePurchase(Order order) {
        if (!order.userHasEnoughBalance()) {
            return Status.USER_HAS_LOW_BALANCE;
        }

        if (!shopContainsAllItems(order.getOrderedItems())) {
            return Status.NON_EXISTING_ITEM;
        }

        Map<Long, Long> orderedItemQuantities = countItems(order.getOrderedItems());
        
        if (!shopContainsSufficientItemQuantities(orderedItemQuantities)) {
            return Status.NOT_ENOUGH_ITEMS_IN_THE_SHOP;
        }

        User user = order.getUser();
        user.setBalance(user.getBalance() - order.countTotalOrdersCost());

        removeItemsFromStore(orderedItemQuantities);
        return Status.OK;
    }

    private boolean shopContainsAllItems(List<Item> items) {
        return items.stream()
                .allMatch(item -> stock.containsKey(item.getId()));
    }

    private boolean shopContainsSufficientItemQuantities(Map<Long, Long> requiredCounts) {
        return requiredCounts.entrySet().stream()
                .allMatch(entry -> stock.get(entry.getKey()) >= entry.getValue());
    }

    private Map<Long, Long> countItems(List<Item> items) {
        return items.stream()
                .collect(Collectors.groupingBy(Item::getId, Collectors.counting()));
    }

    private void removeItemsFromStore(Map<Long, Long> items) {
        items.forEach(this::decreaseItemQuantity);
    }

    private void decreaseItemQuantity(long itemId, long newQuantity) {
       stock.replace(itemId, stock.get(itemId) - newQuantity);
    }
}
