package com.driver;


import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {
    //Creating hashmaps

    //order database
    private Map<String,Order> orderHashMap;

    //delivery partner database
    private Map<String,DeliveryPartner> partnerHashMap;

    //order-partner pair
    private Map<String, List<String>> partnerOrderHashMap;

    private Set<String> orderNotAssigned;


    public OrderRepository() {
        this.orderHashMap = new HashMap<>();
        this.partnerHashMap = new HashMap<>();
        this.partnerOrderHashMap = new HashMap<>();
        this.orderNotAssigned =  new HashSet<>();
    }

    public void addOrder(Order order){
        orderHashMap.put(order.getId(),order);
        orderNotAssigned.add(order.getId());
    }

    public void addPartner(String partnerId){
        partnerHashMap.put(partnerId,new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
        partnerHashMap.get(partnerId).setNumberOfOrders(partnerHashMap.get(partnerId).getNumberOfOrders()+1);
        if(partnerOrderHashMap.containsKey(partnerId)){
            List<String> orderList = partnerOrderHashMap.get(partnerId);
            orderList.add(orderId);
            orderNotAssigned.remove(orderId);
            return;
        }

        partnerOrderHashMap.put(partnerId,new ArrayList<>(Arrays.asList(orderId)));
        orderNotAssigned.remove(orderId);
    }

    public Order getOrderById(String orderId){
        return orderHashMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return partnerHashMap.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
        return partnerOrderHashMap.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orderList = new ArrayList<>();
        List<String> orderIdList = partnerOrderHashMap.get(partnerId);
        for(String order : orderIdList){
            orderList.add(orderHashMap.get(order).getId());
        }
        return orderList;
    }

    public List<String> getAllOrders(){
        Collection<Order> values = orderHashMap.values();
        List<String> orderList = new ArrayList<>();
        for(Order o : values){
            orderList.add(o.getId());
        }
        return orderList;
    }

    public int getCountOfUnassignedOrders(){
        return orderNotAssigned.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int numericalTime = Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3,5));
        int count = 0;
        for(String orderId : partnerOrderHashMap.get(partnerId)){
            if(orderHashMap.get(orderId).getDeliveryTime()>numericalTime){
                count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int latestTime = 0;
        if(partnerOrderHashMap.containsKey(partnerId)){
            for(String currOrderId : partnerOrderHashMap.get(partnerId)){
                if(orderHashMap.get(currOrderId).getDeliveryTime()>latestTime){
                    latestTime = orderHashMap.get(currOrderId).getDeliveryTime();
                }
            }
        }
//        int minute = 0;
//        for(int i=1; i<=60; i++){
//            if((latestTime - i)%60 == 0){
//                minute = i;
//                break;
//            }
//        }
//        int restOfTime = latestTime - minute;
        int hours = latestTime/60;
        int minute = latestTime%60;

        String strhours = Integer.toString(hours);
        if(strhours.length()==1){
            strhours = "0"+strhours;
        }

        String minutes = Integer.toString(minute);
        if(minutes.length()==1){
            minutes = "0" + minutes;
        }
        return strhours + ":" + minutes;

    }

    public void deletePartnerById(String partnerId){
        if(!partnerOrderHashMap.isEmpty()){
            orderNotAssigned.addAll(partnerOrderHashMap.get(partnerId));
        }
        partnerOrderHashMap.remove(partnerId);
        partnerHashMap.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        orderHashMap.remove(orderId);
        if(orderNotAssigned.contains(orderId)){
            orderNotAssigned.remove(orderId);
        }
        else {
            for(List<String> listofOrderIds : partnerOrderHashMap.values()){
                listofOrderIds.remove(orderId);
            }
//            List<String> listOfObjectIds = new ArrayList<>();
//            partnerOrderMap.values().forEach(listOfObjectIds::addAll);
//            listOfObjectIds.remove(orderId);
        }
    }

}