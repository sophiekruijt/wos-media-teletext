package nl.wos.teletext.core;

public class FastText {

    private static final String defaultRedButtonText = "Nieuws";
    private static final String defaultGreenButtonText = "Sport";
    private static final String defaultYellowButtonText = "TV";
    private static final String defaultBlueButtonText = "Weer";

    private static final int defaultRedButtonLink = 101;
    private static final int defaultGreenButtonLink = 600;
    private static final int defaultYellowButtonLink = 200;
    private static final int defaultBlueButtonLink = 700;

    private String redButtonText;
    private int redButtonLink;

    private String greenButtonText;
    private int greenButtonLink;

    private String yellowButtonText;
    private int yellowButtonLink;

    private String blueButtonText;
    private int blueButtonLink;

    public FastText() {
        this.redButtonText = defaultRedButtonText;
        this.redButtonLink = defaultRedButtonLink;
        this.greenButtonText = defaultGreenButtonText;
        this.greenButtonLink = defaultGreenButtonLink;
        this.yellowButtonText = defaultYellowButtonText;
        this.yellowButtonLink = defaultYellowButtonLink;
        this.blueButtonText = defaultBlueButtonText;
        this.blueButtonLink = defaultBlueButtonLink;
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