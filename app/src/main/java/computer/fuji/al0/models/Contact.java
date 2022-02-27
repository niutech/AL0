package computer.fuji.al0.models;

import java.util.ArrayList;

public class Contact {
    private String id;
    private String name;
    private String phoneNumber;
    private ArrayList<String> alternateNumbers;

    public Contact (String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.alternateNumbers = new ArrayList<>();
    }

    // getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ArrayList<String> getAlternateNumbers () {
        return this.alternateNumbers;
    }

    // setters
    public void setName (String name) {
        this.name = name;
    }

    public void setPhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void addAlternateNumber (String alternateNumber) {
        this.alternateNumbers.add(alternateNumber);
    }
}
