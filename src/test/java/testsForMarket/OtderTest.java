package testsForMarket;

import entities.Item;
import entities.Order;
import entities.User;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OtderTest {

    private Map<Item, Long> goods = new HashMap<Item, Long>();
    Item validItem = new Item(1L, "Kettle", 4.99d);
    Item valid2Item = new Item(2L, "Helmet", 5.99d);
    Item valid3Item = new Item(3L, "Hummer", 6.99d);
    User user = new User("John", 200d);

    @Test(priority = 1)
    public void demo() {
        Order order = new Order(user, Arrays.asList());

        assert order.getOrderedItems().isEmpty() :
                "Cart should be empty!" + ", actual: " + order.getOrderedItems().toString();
    }


    @Test(priority = 1)
    public void cartShouldBeEmpty() {
        Order order = new Order(user, Arrays.asList());

        assert order.getOrderedItems().isEmpty() :
                "Cart should be empty!" + ", actual: " + order.getOrderedItems().toString();
    }

    @Test(priority = 1)
    public void orderItemsShouldMatch() {
        Order order = new Order(user, Arrays.asList(validItem, valid2Item, valid3Item));

        Assert.assertEquals(order.getOrderedItems(), Arrays.asList(validItem, valid2Item, valid3Item));
    }


    @Test(priority = 1)
    public void orderTotalShouldMatch() {
        Order order = new Order(user, Arrays.asList(validItem, validItem, validItem));

        Assert.assertEquals( order.countTotalOrdersCost(), 4.99*3);
    }
}
