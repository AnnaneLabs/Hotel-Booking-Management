package model;

import java.math.BigDecimal;

public class Room {

    private String roomNumber;
    private RoomType type;
    private int capacity;
    private BigDecimal pricePerNight;
    private RoomStatus status;

    public Room(String roomNumber, RoomType type, int capacity,BigDecimal pricePerNight, RoomStatus status) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.status = status;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public RoomStatus getStatus() {
        return status;
    }


    public void setType(RoomType type) {
        this.type = type;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    // toString — controls what gets printed if you do System.out.println(someRoom).
    // Without this, Java would print something unreadable like "Room@1b6d3586".
    @Override
    public String toString() {
        return "Room{" +
                "roomNumber='" + roomNumber + '\'' +
                ", type=" + type +
                ", capacity=" + capacity +
                ", pricePerNight=" + pricePerNight +
                ", status=" + status +
                '}';
    }
}