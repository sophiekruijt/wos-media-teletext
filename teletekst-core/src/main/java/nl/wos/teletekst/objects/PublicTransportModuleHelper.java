package nl.wos.teletekst.objects;

import nl.wos.teletekst.core.TeletextSubpage;

import java.text.SimpleDateFormat;
import java.util.List;

public class PublicTransportModuleHelper
{
    public static void addContentToPage(TeletextSubpage page, List<TrainDeparture> departures, String stationName)
    {
        try {
            page.setTextOnLine(0,"\u0002"+stationName.toUpperCase());
            page.setTextOnLine(1,"\u0003TIJD       BESTEMMING       SPOOR TYPE");

            if(departures.size() == 0) {
                page.setTextOnLine(3, "Op dit station zijn momenteel geen");
                page.setTextOnLine(4, "vertrekkende treinen");
            }

            int line = 2;
            for(int i=0; i<departures.size(); i++) {
                if(line > 17) {
                    break;
                }

                // Change 'Intercity' or 'Sprinter' to 'IC' and 'SPR'.
                String trainType = departures.get(i).getTrainType();
                if(trainType.equals("Intercity")) {
                    trainType = "IC";
                }
                else if(trainType.equals("Sprinter")) {
                    trainType = "SPR";
                }

                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                String departureTime = format.format(departures.get(i).getDepartureTime()).toString();
                String departureDelay = departures.get(i).getDepartureDelay().replace("PT", "+");
                String destination = departures.get(i).getFinalDestination();
                String opmerking = "";
                //for(int j=0; j<departures.get(i).getRouteText().length(); j++)
                //{
                //    //opmerking += departures.get(i).OpmerkingList1[j];
                //}

                if(destination.length() >= 18) {
                    destination = destination.substring(0,18);
                }

                String vertrekspoor = (departures.get(i).isChanged()) ?
                        ""+departures.get(i).getDepartureTrack() :
                        ""+departures.get(i).getDepartureTrack();

                String line1 = String.format("%-9s %-19s %-4s %5s", "\u0003"+departureTime + departureDelay,"\u0007"+destination, vertrekspoor ,"\u0006"+trainType).toString();
                page.setTextOnLine(line, line1);
                line++;
                if(departures.get(i).isChanged()) {
                    opmerking += " Gewijzigd vertrekspoor!";
                }
                if(!(opmerking != null || opmerking.isEmpty() || opmerking.equals("geen")))
                {
                    page.setTextOnLine(line,""+opmerking);
                    line++;
                    line++;
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println("Something is wrong!!!!!!!!!!!!!!! " + ex.toString());
        }
    }
}