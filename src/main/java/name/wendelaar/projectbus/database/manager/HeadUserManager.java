package name.wendelaar.projectbus.database.manager;

import name.wendelaar.projectbus.database.models.Item;
import name.wendelaar.projectbus.database.models.User;
import name.wendelaar.projectbus.database.models.UserData;
import name.wendelaar.projectbus.main.LlsApi;
import name.wendelaar.projectbus.main.MainManager;
import name.wendelaar.projectbus.view.ViewState;
import name.wendelaar.snowdb.data.DataObject;
import name.wendelaar.snowdb.manager.Manager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HeadUserManager implements IUserManager, IAuthenticationManager {

    private MainManager mainManager;
    private User currentUser;

    public HeadUserManager(MainManager mainManager) {
        this.mainManager = mainManager;
    }

    @Override
    public void createUser(DataObject userObject, DataObject userDataObject) {
        if (userObject == null) {
            return;
        }

        Object plainPassword = userObject.get("user.password");
        userObject.set("user.password", BCrypt.hashpw(plainPassword.toString(), BCrypt.gensalt()));

        Manager.saveDataObject(userObject);

        if (userDataObject == null) {
            return;
        }

        userDataObject.set("user_data_personal.user_id", userObject.get("user.id"));

        Manager.saveDataObject(userDataObject);
    }

    @Override
    public void deleteUser(User user) {
        if (user == null) {
            return;
        }

        IItemManager itemManager = LlsApi.getItemManager();
        Collection<Item> items = itemManager.getItemsOfUser(user);
        for (Item item : items) {
            itemManager.returnItem(item);
        }

        Manager.deleteModel(user);
    }

    @Override
    public void disableUser(User user) {
        user.setAccountDisabled(!user.isAccountDisabled());
        Manager.saveModel(user);
    }

    @Override
    public User getUser(int id) {
        User user = null;
        try {
            DataObject userData = Manager.create().prepare("SELECT * FROM user WHERE id = ? LIMIT 1", id).findOne();
            if (userData == null) {
                return null;
            }

            user = new User(userData);
        } catch (SQLException ex) {
            ex.printStackTrace(); //TODO: Good error handling!
        }
        return user;
    }

    @Override
    public UserData getUserData(int id) {
        UserData data = null;
        try {
            DataObject userData = Manager.create().prepare("SELECT * FROM user_data_personal WHERE user_id = ? LIMIT 1", id).findOne();
            if (userData == null) {
                return null;
            }

            data = new UserData(userData, null);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    @Override
    public User getUser(String username) {
        User user = null;
        try {
            DataObject userData = Manager.create().prepare("SELECT * FROM user WHERE username = ? LIMIT 1", username).findOne();
            if (userData == null) {
                return null;
            }

            user = new User(userData);
        } catch (SQLException ex) {
            ex.printStackTrace(); //TODO: Good error handling!
        }
        return user;
    }

    @Override
    public Collection<User> getUsersExceptOne(User user) {
        int id;
        if (user == null || (id = user.getId()) == 0) {
            return getUsers();
        }

        List<User> users = new ArrayList<>();
        try {
            List<DataObject> dataObjects = Manager.create().prepare("SELECT * FROM user WHERE user.id <> ?", id).find();
            for (DataObject dataObject : dataObjects) {
                users.add(new User(dataObject));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); //TODO: Good error handling!
        }
        return users;
    }

    @Override
    public Collection<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            List<DataObject> dataObjects = Manager.create().prepare("SELECT * FROM user").find();
            for (DataObject dataObject : dataObjects) {
                users.add(new User(dataObject));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); //TODO: Good error handling!
        }
        return users;
    }

    @Override
    public boolean authenticate(String email, String password) {
        User user = null;
        try {
            DataObject dataObject = Manager.create().prepare("SELECT * FROM user WHERE email = ? LIMIT 1", email).findOne();
            if (dataObject == null) {
                return false;
            }

            user = new User(dataObject);
        } catch (SQLException ex) {
            ex.printStackTrace(); //TODO: Good error handling!
        }
        if (user == null || !user.hasSamePassword(password)) {
            return false;
        }
        currentUser = user;

        return true;
    }

    @Override
    public void logout() {
        currentUser = null;
        mainManager.getViewManager().changeState(ViewState.LOGIN);
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }
}
