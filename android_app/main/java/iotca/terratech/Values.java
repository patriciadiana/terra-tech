package iotca.terratech;

public class Values {
    private static Values instance;
    private int pumps, interval;
    private String flower_type, soil_status;
    private float humidity, temperature;

    private Values() {
        // Initialize your variables here
        pumps = 2;
        interval = 1;
        flower_type = "";
        soil_status = "Dry";
        humidity = 74;
        temperature = 23;
    }

    public String getSoil_status() {
        return soil_status;
    }

    public void setSoil_status(String soil_status) {
        this.soil_status = soil_status;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public static final String USERNAME = "barsi";
    public static final String PASSWORD = "barsiemic";
    public static final int PORT = 22;
    public static final String HOST = "192.168.1.163";
    //    public static final String HOST = "192.168.43.207";

    public static synchronized Values getInstance() {
        if (instance == null) {
            instance = new Values();
        }
        return instance;
    }

    public String getFlower_type() {
        return flower_type;
    }

    public void setFlower_type(String flower_type) {
        this.flower_type = flower_type;
    }

    public int getPumps() {
        return pumps;
    }

    public void setPumps(int pumps) {
        this.pumps = pumps;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}

