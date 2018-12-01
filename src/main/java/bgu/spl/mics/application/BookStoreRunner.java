package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        String configFilePath = args[0];
        String content = "";
        try {
            File f = new File(configFilePath);
            content = new String(Files.readAllBytes(f.toPath()));
        } catch (IOException e) {
            System.out.println("could not read config file");
        }
        Gson gson = new Gson();
        JsonElement jelement = new JsonParser().parse(content);
        JsonObject allJsonObj = jelement.getAsJsonObject();
        // load initial store inventory and resources
        Inventory.getInstance().load(getArrayFromJson("initialInventory", BookInventoryInfo.class, allJsonObj, gson)
                .toArray(new BookInventoryInfo[0]));
        ResourcesHolder.getInstance().load(getArrayFromJson("initialResources", DeliveryVehicle.class, allJsonObj, gson)
                .toArray(new DeliveryVehicle[0]));
        // initiate time service
        JsonObject servicesJsonObj = allJsonObj.getAsJsonObject("services");
        gson.fromJson(servicesJsonObj.get("time"), TimeService.class);
        // initiate other services
        fireService(SellingService::new, gson.fromJson(servicesJsonObj.get("selling"), Integer.class));
        fireService(InventoryService::new, gson.fromJson(servicesJsonObj.get("inventoryService"), Integer.class));
        fireService(LogisticsService::new, gson.fromJson(servicesJsonObj.get("logistics"), Integer.class));
        fireService(ResourceService::new, gson.fromJson(servicesJsonObj.get("resourcesService"), Integer.class));
        List<Customer> customers = getArrayFromJson("customers", Customer.class, servicesJsonObj, gson);
    }

    private static void fireService(Runnable runnable, int numberToStart) {
        for (int i = 0; i < numberToStart; i++) {
            new Thread(runnable).start();
        }
    }

    private static <T> List<T> getArrayFromJson(String nameInJson, Class<T> typeOfInnerItem, JsonObject jsonObject, Gson gson) {
        JsonArray jarray = jsonObject.getAsJsonArray(nameInJson);
        List<T> initialInv = new ArrayList<>(jarray.size());
        for (JsonElement jsonElement : jarray) {
            initialInv.add(gson.fromJson(jsonElement, typeOfInnerItem));
        }
        return initialInv;
    }
}
