package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.c4;

import com.structurizr.Workspace;
import com.structurizr.analysis.*;
import com.structurizr.api.StructurizrClient;
import com.structurizr.model.*;
import com.structurizr.model.Container;
import com.structurizr.view.ComponentView;
import com.structurizr.view.Styles;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;
import com.structurizr.view.Shape;
import org.junit.jupiter.api.Test;


public class GenerateModel {

    private final static int WORKSPACE_ID = 88409;
    private final static String API_KEY = "d9eb5c78-2009-4a6c-8ff3-b8836721535d";
    private final static String API_SECRET = "e1685417-6dd7-4857-9ad2-3816a964b94d";

    @Test
    public void generateModel() throws Exception {
        Workspace workspace = new Workspace("BipSync", "BipSync Project");
        Model model = workspace.getModel();

        SoftwareSystem bypsinc = model.addSoftwareSystem("Bipsync", "Bipsync on-boarding portal");
        Person HR = model.addPerson("HR", "Human Resources Manager");
        Person NewEmployee = model.addPerson("New Employee", "New employee on-board");
        Person DepartmentMember = model.addPerson("Department Member", "Department member");

        HR.uses(bypsinc, "Uses");
        NewEmployee.uses(bypsinc, "Uses");
        DepartmentMember.uses(bypsinc, "Uses");

        Container webApp = bypsinc.addContainer("Spring Boot Application", "The web application", "Embedded web container. Tomcat 7.0");
        Container relationalDB = bypsinc.addContainer("Relational Database", "Stores data", "MySQL");

        HR.uses(webApp, "Uses", "HTTPS");
        NewEmployee.uses(webApp, "Uses", "HTTPS");
        DepartmentMember.uses(webApp, "Uses", "HTTPS");

        webApp.uses(relationalDB, "Reads from and writes to", "JDBC, port 3306");

        ComponentFinder componentFinder = new ComponentFinder(
                webApp,
                "uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4",
                new SpringComponentFinderStrategy(
                        new ReferencedTypesSupportingTypesStrategy()
                )
        );
        componentFinder.findComponents();

        ComponentFinder componentFinderByAnnotation = new ComponentFinder(
                webApp,
                "uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4",
                new StructurizrAnnotationsComponentFinderStrategy()
        );
        componentFinderByAnnotation.findComponents();

        webApp.getComponents().stream()
                .filter(c -> c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_REPOSITORY))
                .forEach(c -> c.uses(relationalDB, "Reads from and writes to", "JDBC"));

        ViewSet viewSet = workspace.getViews();
        SystemContextView contextView = viewSet.createSystemContextView(bypsinc, "context", "The System Context diagram for Bipsync");
        contextView.addAllSoftwareSystems();
        contextView.addAllPeople();
        contextView.enableAutomaticLayout();

        ComponentView componentView = viewSet.createComponentView(webApp, "components", "The Components diagram for Bipsync");
        componentView.addAllComponents();
        componentView.addAllPeople();
        componentView.add(relationalDB);
        componentView.enableAutomaticLayout();

        bypsinc.addTags("Bipsync");
        webApp.getComponents().stream().filter(c ->
                c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_SERVICE)).forEach(c -> c.addTags("Spring Service"));
        webApp.getComponents().stream().filter(c ->
                c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_REPOSITORY)).forEach(c -> c.addTags("Spring Repository"));
        relationalDB.addTags("Database");

        Styles styles = viewSet.getConfiguration().getStyles();
        styles.addElementStyle("Bipsync").background("#6CB33E").color("#ffffff");
        styles.addElementStyle(Tags.PERSON).background("#519823").color("#ffffff").shape(Shape.Person);
        styles.addElementStyle(Tags.CONTAINER).background("#91D366").color("#ffffff");
        styles.addElementStyle("Database").shape(Shape.Cylinder);

        styles.addElementStyle("Spring Rest Controller").background("#D4FFC0").color("#000000");
        styles.addElementStyle("Spring Service").background("#6CB33E").color("#000000");
        styles.addElementStyle("Spring Repository").background("#95D46C").color("#000000");

        StructurizrClient structurizrClient = new StructurizrClient(API_KEY, API_SECRET);
        structurizrClient.putWorkspace(WORKSPACE_ID, workspace);

    }
}
