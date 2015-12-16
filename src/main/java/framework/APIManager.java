package framework;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import common.EnumKeys;
import entities.Location;
import entities.Meeting;
import entities.Resource;
import java.util.ArrayList;
import com.jayway.restassured.response.Response;
import org.json.JSONArray;
import static com.jayway.restassured.RestAssured.given;

/**
 * Created with IntelliJ IDEA.
 * User: jhasmanyquiroz
 * Date: 12/11/15
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class APIManager {
    private String token;
    private static APIManager instance;

    protected APIManager() {
        initialize();
    }

    public static APIManager getInstance() {
        if(instance == null)
            instance = new APIManager();
        return instance;
    }
    private void initialize() {
        RestAssured.baseURI = "https://172.20.208.216:4040";
        RestAssured.useRelaxedHTTPSValidation();
        token = getToken();
    }

    private String getToken() {
        Response response = given()
                .parameters("username", "BrayanRosas", "password",
                            "Client123", "authentication", "local")
                .post("/login");

        String json = response.asString();
        JsonPath jp = new JsonPath(json);
        return jp.get("token");
    }

    public Resource createResourceByName(String name) {
        Resource resource = new Resource();
        Response response = given()
                .header("Authorization", "jwt " + token)
                .parameters(EnumKeys.RESOURCEKEY.name, name, EnumKeys.RESOURCEKEY.description, "",
                            EnumKeys.RESOURCEKEY.customName, name, EnumKeys.RESOURCEKEY.from, "",
                            EnumKeys.RESOURCEKEY.icon, "")
                .post("/resources")
        ;

        String json = response.asString();
        JsonPath jp = new JsonPath(json);
        resource = setResource((String)jp.get("_id"), (String)jp.get("name"), (String)jp.get("description"), (String)jp.get("customName"), (String)jp.get("fontIcon"));

        return resource;
    }

    private void createLocationByName(String name) {
        given()
                .header("Authorization", "jwt " + token)
                .parameters("customName", name, "name", name,
                            "description", "")
                .post("/locations")
                ;
    }

    private void deleteResourceByID(String id) {
        given()
            .header("Authorization", "jwt " + token)
            .parameters("id", id)
            .delete("/resources/"+id)
        ;
    }

    private void deleteLocationByID(String _id) {
        given()
                .header("Authorization", "jwt " + token)
                .parameters("id", _id)
                .delete("/locations/"+_id)
        ;
    }

    private Resource setResource(String _id, String name, String description,
                                 String customName, String fontIcon) {
        Resource resource = new Resource();

        resource.setID(_id);
        resource.setName(name);
        resource.setDescription(description);
        resource.setDisplayName(customName);
        resource.setIcon(fontIcon);

        return resource;
    }

    private Location setLocation(String _id, String name, String description,
                                 String customName, String path) {
        Location location = new Location();

        location.setId(_id);
        location.setName(name);
        location.setDescription(description);
        location.setDisplayName(customName);
        location.setParentLocation(path);

        return location;
    }

    public ArrayList<Resource> createResourcesByName(ArrayList<String> resourcesName) {
        ArrayList<Resource> resources = new ArrayList<>();
        for (String name : resourcesName) {
            resources.add(createResourceByName(name));
        }
        return resources;
    }

    public void createLocationsByName(ArrayList<String> locationsName) {
        for (String name : locationsName)
            createLocationByName(name);
    }

    public void deleteResourcesById(ArrayList<Resource> resources) {
        for (Resource resource : resources) {
            deleteResourceByID(resource.getID());
        }
    }

    public void deleteLocationByID(ArrayList<String> locationsID) {
        for (String _id : locationsID)
            deleteLocationByID(_id);
    }

    public Resource getResourceByID(String id) {
        Response response = given().when().get("/resources/"+id);
        String json = response.asString();
        JsonPath jp = new JsonPath(json);

        return setResource((String)jp.get(EnumKeys.RESOURCEKEY._id), (String)jp.get(EnumKeys.RESOURCEKEY.name),
                            (String)jp.get(EnumKeys.RESOURCEKEY.description), (String)jp.get(EnumKeys.RESOURCEKEY.customName),
                            (String)jp.get("fontIcon"));
    }

    public Location getLocationByID(String _id) {
        Response response = given().when().get("/locations/"+_id);
        String json = response.asString();
        JsonPath jp = new JsonPath(json);

        return setLocation((String)jp.get("_id"), (String)jp.get("name"),
                            (String)jp.get("description"),(String)jp.get("customName"),
                            (String)jp.get("path"));
    }

    public ArrayList<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();

        Response response = given().when().get("/resources");
        JSONArray jsonArray = new JSONArray(response.asString());

        for (int indice = 0; indice < jsonArray.length(); indice++) {
            resources.add(setResource(jsonArray.getJSONObject(indice).getString("_id"),
                            jsonArray.getJSONObject(indice).getString("name"),
                            jsonArray.getJSONObject(indice).getString("description"),
                            jsonArray.getJSONObject(indice).getString("customName"),
                            jsonArray.getJSONObject(indice).getString("fontIcon"))
                          );
        }
        return resources;
    }

    public ArrayList<Location> getLocations() {
        ArrayList<Location> locations = new ArrayList<>();

        Response response = given().when().get("/locations");
        JSONArray jsonArray = new JSONArray(response.asString());

        for (int indice = 0; indice < jsonArray.length(); indice++) {
            locations.add(setLocation(jsonArray.getJSONObject(indice).getString("_id"),
                    jsonArray.getJSONObject(indice).getString("name"),
                    jsonArray.getJSONObject(indice).getString("description"),
                    jsonArray.getJSONObject(indice).getString("customName"),
                    jsonArray.getJSONObject(indice).getString("path"))
            );
        }
        return locations;
    }

    public Meeting createMeeting(String organizer,String title,String start,String end,String location,String roomEmail,String resources,String attendees,String roomId ) {

        ArrayList<String> resourcesValues = new ArrayList<String>();
        resourcesValues.add(resources);

        ArrayList<String> attendeesValues = new ArrayList<String>();
        attendeesValues.add(attendees);

        Meeting meeting = new Meeting();


        Response response = given()
                .header("Authorization", "Basic amhhc21hbnkucXVpcm96OkNsaWVudDEyMw==")
                .parameters("organizer",organizer,"title",title,"start",start,"end",end,"location",location,"roomEmail",roomEmail,"resources",resourcesValues,"attendees",attendeesValues)
                .post("/services/565f3f449c27d64812f72af0"+"/rooms/" + roomId + "/meetings");
        String json = response.asString();
        JsonPath jp = new JsonPath(json);

        System.out.println("******************RESPONSE - "+json);
        //meeting = setMeeting((String)jp.get("organizer"), (String)jp.get("title"), (String)jp.get("start"), (String)jp.get("end"));

        return meeting;
    }

    private Meeting setMeeting(String organizer,String title,String from,String to){

        Meeting meeting = new Meeting();
        meeting.setOrganizer(organizer);
        meeting.setTitle(title);
        meeting.setFrom(from);
        meeting.setTo(to) ;
        return meeting;

    }

    private String getServiceId(){
        Response response = given().header("Authorization", "jwt " + token).get("/services");
        String json = response.asString();
        JsonPath jp = new JsonPath(json);
       // System.out.println("JSON - "+json+"SERVICES ID - "+jp.get("_id"));

        return "565f3f449c27d64812f72af0";
    }

}