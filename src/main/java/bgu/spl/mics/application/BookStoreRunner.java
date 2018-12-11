package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CountDownLatch;

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
        JsonObject allJsonObj = new JsonParser().parse(content).getAsJsonObject();
        loadStaticResources(gson, allJsonObj);
        // initiate other services
        JsonObject servicesJsonObj = allJsonObj.getAsJsonObject("services");
        int numOfServices = getNumOfServices(gson, servicesJsonObj);
        CountDownLatch countDownLatch = new CountDownLatch(numOfServices);
        List<MicroService> services = extractServices(gson, servicesJsonObj, countDownLatch);
        List<Thread> threadPool = new ArrayList<>(services.size());
        services.forEach(service -> {
            Thread thread = new Thread(service);
            threadPool.add(thread);
            thread.start();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println("Something reeeeealy bad happend");
        }
        TimeService time = gson.fromJson(servicesJsonObj.get("time"), TimeService.class);
        Thread timeServiceThread = new Thread(time);
        timeServiceThread.start();
        try {
            // waits for time service to terminate
            timeServiceThread.join();
        } catch (InterruptedException e) {
            System.out.println("should not happen");
        }
        // trigger interruption
        threadPool.forEach(Thread::interrupt);
        //todo:elad make sure all service initialize before time service
        // initiate time service
        //todo: write to output files
    }

    private static int getNumOfServices(Gson gson, JsonObject servicesJsonObj) {
        int counter = 0;
        counter += gson.fromJson(servicesJsonObj.get("selling"), Integer.class);
        counter += gson.fromJson(servicesJsonObj.get("inventoryService"), Integer.class);
        counter += gson.fromJson(servicesJsonObj.get("logistics"), Integer.class);
        counter += gson.fromJson(servicesJsonObj.get("resourcesService"), Integer.class);
        List<Customer> customers = getArrayFromJson("customers", Customer[].class, servicesJsonObj, gson);
        counter += customers.size();
        return counter;
    }

    private static List<MicroService> extractServices(Gson gson, JsonObject servicesJsonObj,CountDownLatch countDownLatch) {
        List<MicroService> services = new LinkedList<>();
        for (int i = 0; i < gson.fromJson(servicesJsonObj.get("selling"), Integer.class); i++) {
            services.add(new SellingService(countDownLatch));
        }
        for (int i = 0; i < gson.fromJson(servicesJsonObj.get("inventoryService"), Integer.class); i++) {
            services.add(new InventoryService(countDownLatch));
        }
        for (int i = 0; i < gson.fromJson(servicesJsonObj.get("logistics"), Integer.class); i++) {
            services.add(new LogisticsService(countDownLatch));
        }
        for (int i = 0; i < gson.fromJson(servicesJsonObj.get("resourcesService"), Integer.class); i++) {
            services.add(new ResourceService(countDownLatch));
        }
        List<Customer> customers = getArrayFromJson("customers", Customer[].class, servicesJsonObj, gson);
        customers.sort(Comparator.comparing(Customer::getId));
        customers.forEach(customer -> services.add(new APIService(customer, countDownLatch)));
        return services;
    }

    private static void loadStaticResources(Gson gson, JsonObject allJsonObj) {
        // load initial store inventory and resources
        Inventory.getInstance().load(getArrayFromJson("initialInventory", BookInventoryInfo[].class, allJsonObj, gson)
                .toArray(new BookInventoryInfo[0]));
        JsonArray initialResources = allJsonObj.getAsJsonArray("initialResources");
        JsonObject vehicles = initialResources.get(0).getAsJsonObject();
        DeliveryVehicle[] deliveryVehicles = gson.fromJson(vehicles.getAsJsonArray("vehicles"), DeliveryVehicle[].class);
        ResourcesHolder.getInstance().load(deliveryVehicles);
    }

    private static <T> List<T> getArrayFromJson(String nameInJson, Class<T[]> typeOfInnerItem, JsonObject jsonObject, Gson gson) {
        JsonArray jarray = jsonObject.getAsJsonArray(nameInJson);
        return Arrays.asList(gson.fromJson(jarray, typeOfInnerItem));
    }
}
