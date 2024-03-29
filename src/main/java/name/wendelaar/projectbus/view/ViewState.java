package name.wendelaar.projectbus.view;

public enum ViewState {
    LOGIN("login.fxml"),
    INDEX_DEFAULT("indexUser.fxml"),
    INDEX_LIBRARIAN("indexLibrarian.fxml");

    private String fileName;

    ViewState(String fileName) {
        this.fileName = fileName;
    }

    public String getViewFile() {
        return fileName;
    }

    public static ViewState getByName(String name) {
        for (ViewState state : ViewState.values()) {
            if (state.name().equalsIgnoreCase(name)) {
                return state;
            }
        }
        return null;
    }
}
