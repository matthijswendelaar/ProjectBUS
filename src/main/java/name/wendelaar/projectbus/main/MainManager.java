package name.wendelaar.projectbus.main;

import name.wendelaar.projectbus.database.concurrency.DatabaseThreadFactory;
import name.wendelaar.projectbus.database.manager.*;
import name.wendelaar.projectbus.view.IViewManager;
import name.wendelaar.projectbus.view.ViewManager;
import name.wendelaar.projectbus.view.ViewState;
import name.wendelaar.snowdb.SnowDB;
import name.wendelaar.snowdb.data.factory.PerformanceDataObjectFactory;
import name.wendelaar.snowdb.exceptions.SnowDBException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainManager implements IHeadController {

    //Thread related fields
    private ExecutorService executorService;

    //Manager related fields
    private HeadUserManager userManager;
    private IReservationManager reservationManager;
    private IItemManager itemManager;
    private IItemAttributeManager itemAttributeManager;

    //View related fields
    private ViewState state;
    private ViewManager viewManager;

    public MainManager(ViewState state) {
        try {
            SnowDB.getInstance().initialize();
        } catch (SnowDBException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        executorService = Executors.newFixedThreadPool(5, new DatabaseThreadFactory("DB_Thread_"));

        LlsApi.receiveController(this);
        this.state = state;
        this.userManager = new HeadUserManager(this);
        this.reservationManager = new ReservationManager(this);
        this.itemManager = new ItemManager(this);
        this.itemAttributeManager = new ItemAttributeManager();
        ViewManager.getInstance();
    }

    public void setViewManager(ViewManager viewManager) {
        if (this.viewManager != null) {
            throw new IllegalStateException("View Manager already initialized");
        }
        this.viewManager = viewManager;
    }

    public ViewState getStartupState() {
        return state;
    }

    @Override
    public ViewState getCurrentState() {
        return viewManager.getCurrentState();
    }

    @Override
    public IUserManager getUserManager() {
        return userManager;
    }

    @Override
    public IAuthenticationManager getAuthManager() {
        return userManager;
    }

    @Override
    public IReservationManager getReservationManager() {
        return reservationManager;
    }

    @Override
    public IItemManager getItemManager() {
        return itemManager;
    }

    @Override
    public IViewManager getViewManager() {
        return viewManager;
    }

    @Override
    public IItemAttributeManager getItemAttributeManager() {
        return itemAttributeManager;
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }
}
