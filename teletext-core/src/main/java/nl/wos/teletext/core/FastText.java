package nl.wos.teletext.core;

import nl.wos.teletext.entity.PropertyManager;

import java.util.Properties;

public class FastText {

    private Properties properties = PropertyManager.getProperties();

    private String redButtonText;
    private int redButtonLink;

    private String greenButtonText;
    private int greenButtonLink;

    private String yellowButtonText;
    private int yellowButtonLink;

    private String blueButtonText;
    private int blueButtonLink;

    public FastText() {
        this.redButtonText = properties.getProperty("defaultRedButtonText");
        this.redButtonLink = Integer.parseInt(properties.getProperty("defaultRedButtonLink"));
        this.greenButtonText = properties.getProperty("defaultGreenButtonText");
        this.greenButtonLink = Integer.parseInt(properties.getProperty("defaultGreenButtonLink"));
        this.yellowButtonText = properties.getProperty("defaultYellowButtonText");
        this.yellowButtonLink = Integer.parseInt(properties.getProperty("defaultYellowButtonLink"));
        this.blueButtonText = properties.getProperty("defaultBlueButtonText");
        this.blueButtonLink = Integer.parseInt(properties.getProperty("defaultBlueButtonLink"));
    }

    public int getBlueButtonLink() {
        return blueButtonLink;
    }

    public int getRedButtonLink() {
        return redButtonLink;
    }

    public String getGreenButtonText() {
        return greenButtonText;
    }

    public int getGreenButtonLink() {
        return greenButtonLink;
    }

    public String getYellowButtonText() {
        return yellowButtonText;
    }

    public int getYellowButtonLink() {
        return yellowButtonLink;
    }

    public String getBlueButtonText() {
        return blueButtonText;
    }

    public String getRedButtonText() {
        return redButtonText;
    }
}