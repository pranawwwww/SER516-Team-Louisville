package utils;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class SprintSelector {
    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();
    public static void selectSprint(String authToken, String slugURL, ComboBox<String> sprintSelector) {
        try {
            int projectID = Project.getProjectId(authToken, GlobalData.getTaigaURL(), slugURL);
            if (projectID == -1) {
                throw new NoSuchElementException();
            }
            SprintUtils.getMilestoneList(authToken, TAIGA_API_ENDPOINT, projectID);
            List<String> sprints = new ArrayList<>(SprintUtils.getSprints());
            sprintSelector.setItems(FXCollections.observableArrayList(sprints));
        } catch (NoSuchElementException exception) {
            AlertPopup.showAlert("Error", "Please Try a Sprint which has been started.");
        }
    }
}