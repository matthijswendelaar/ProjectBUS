package name.wendelaar.projectbus.database.models;

import name.wendelaar.simplevalidator.ExcepValidator;
import name.wendelaar.snowdb.annotation.Data;
import name.wendelaar.snowdb.data.DataObject;
import name.wendelaar.snowdb.data.DataObjectCollection;
import name.wendelaar.snowdb.data.model.Model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Item extends Model {

    @Data
    protected ItemType itemType;
    @Data
    protected Map<String, ItemAttribute> attributeMap;

    public Item(DataObjectCollection dataObject, String type) {
        super(dataObject, "item");

        ExcepValidator.notNull("Type can not be null",type);
        DataObject itemTypeData = dataObject.getDataObjectByTable("item_type");

        ExcepValidator.notNull("Item type not found in collection", itemTypeData);

        itemType = new ItemType(itemTypeData);
        if (!type.equalsIgnoreCase(itemType.getName())) {
            throw new IllegalArgumentException("item types must match");
        }
    }

    public int getId() {
        return (int) dataObject.get("id");
    }

    public String getName() {
        return (String) dataObject.get("item_name");
    }

    public int getLoanedOutCount() {
        return (int) dataObject.get("loaned_out_count");
    }

    public void setAttributes(Collection<ItemAttribute> attributes) {
        if (attributeMap != null) {
            throw new IllegalStateException("Attributes of item have already been set");
        }
        if (attributes == null || attributes.isEmpty()) {
            throw new IllegalArgumentException("Attribute collection can not be null");
        }

        attributeMap = new HashMap<>();

        for (ItemAttribute attribute : attributes) {
            if (attribute == null) {
                continue;
            }
            attributeMap.put(attribute.getAttributeName(), attribute);
        }
    }

    protected Object getAttributeValue(String name) {
        ExcepValidator.notNull("Attributes not set", attributeMap);
        ItemAttribute attribute = attributeMap.get(name);
        return attribute == null ? null : attribute.getAttributeValue();
    }
}
