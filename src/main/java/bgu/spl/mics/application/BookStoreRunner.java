package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
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
            Thread thread = new Thread(service, String.format("threadOf %s", service.getName()));
            threadPool.add(thread);
            thread.start();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println("Something reeeeealy bad happend");
        }
        // initiate time service
        TimeService timeService = gson.fromJson(servicesJsonObj.get("time"), TimeService.class);
        Thread timeServiceThread = new Thread(timeService);
        timeServiceThread.start();
        System.out.println("system has " + threadPool.size());
        threadPool.forEach(thread -> {
            try {
                System.out.println(String.format("joining thread is: %s", thread.getName()));
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("!!!!! was interupted while waiting for threads to join!!!");
            }
        });

        handleOutput(args, services);
    }

    private static void handleOutput(String[] args, List<MicroService> services) {
        List<APIService> apiServices = services.stream()
                .filter(service -> service instanceof APIService)
                .map(service -> (APIService) service)
                .collect(Collectors.toList());
        printCustomersInSystem(args[1], apiServices);
        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);
        printMoneyRegister(args[4]);
    }

    private static void printMoneyRegister(String fileName) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            objectOutputStream.writeObject(MoneyRegister.getInstance());
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("could not write money register");
        }
    }

    private static void printCustomersInSystem(String fileName, List<APIService> apiServices) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            Map<Integer, Customer> customerById = apiServices.stream()
                    .map(APIService::getCustomer)
                    .collect(Collectors.toMap(Customer::getId, Function.identity()));
            System.out.println(customerById.toString());
            objectOutputStream.writeObject(customerById);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("could not write customers");
        }
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

    private static List<MicroService> extractServices(Gson gson, JsonObject servicesJsonObj, CountDownLatch countDownLatch) {
        int seq = 1;
        List<MicroService> services = new LinkedList<>();
        for (int i = 0; i < gson.fromJson(servicesJsonObj.get("selling"), Integer.class); i++) {
            services.add(new SellingService(countDownLatch, seq));
            seq++;
        }
        for (int i = 0; i < gson.fromJson(servicesJsonObj.get("inventoryService"), Integer.class); i++) {
            services.add(new InventoryService(countDownLatch, seq));
            seq++;
        }
        for (int i = 0; i < gson.fromJson(servicesJsonObj.get("logistics"), Integer.class); i++) {
            services.add(new LogisticsService(countDownLatch, seq));
            seq++;
        }
        for (int i = 0; i < gson.fromJson(servicesJsonObj.get("resourcesService"), Integer.class); i++) {
            services.add(new ResourceService(countDownLatch, seq));
            seq++;
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
