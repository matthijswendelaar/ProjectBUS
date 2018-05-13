package name.wendelaar.projectbus.manager;

import name.wendelaar.projectbus.database.models.Item;
import name.wendelaar.projectbus.database.models.ItemType;
import name.wendelaar.projectbus.database.models.User;

import java.util.Collection;

public interface IItemManager {

    public void addItem(String itemName, ItemType type);

    public void removeItem(Item item);

    public void loanOutItem(User borrower, Item item);

    public Collection<Item> getItems();
}