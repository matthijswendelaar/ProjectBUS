package name.wendelaar.projectbus.database.models;

import name.wendelaar.simplevalidator.MatchValidator;
import name.wendelaar.snowdb.data.DataObject;
import name.wendelaar.snowdb.data.model.Model;

public class UserData extends Model {

    private User user;

    public UserData(DataObject dataObject, User user) {
        super(dataObject, "user_data_personal");
        this.user = user;
        match();
    }

    public String getFirstName() {
        return (String) dataObject.get("first_name");
    }

    public String getLastName() {
        return (String) dataObject.get("last_name");
    }

    public String getCity() {
        return (String) dataObject.get("city");
    }

    public String getStreet() {
        return (String) dataObject.get("street");
    }

    public String getPostalCode() {
        return (String) dataObject.get("postal_code");
    }

    public String getHomeNumber() {
        return (String) dataObject.get("home_number");
    }

    private void match() {
        if (user == null) {
            return;
        }
        Object o = dataObject.get("user_id");
        if (!MatchValidator.match(o, user.getId())) {
            throw new IllegalArgumentException("User does not match with UserData");
        }
    }
}
