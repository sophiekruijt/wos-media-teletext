package nl.wos.teletext.objects;

import nl.wos.teletext.core.TeletextSubpage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PublicTransportModuleHelper
{
    private static final Logger log = Logger.getLogger(PublicTransportModuleHelper.class.getName());

    public static void addContentToPage(TeletextSubpage page, List<TrainDeparture> departures, String stationName)
    {
        try {
            page.setTextOnLine(0,"\u0002" + stationName.toUpperCase());
            page.setTextOnLine(1,"\u0003TIJD        BESTEMMING            SPOOR");

            if(departures.size() == 0) {
                page.setTextOnLine(3, "Op dit station zijn momenteel geen");
                page.setTextOnLine(4, "vertrekkende treinen");
            }

            int line = 2;
            for(TrainDeparture d : departures) {
                if(line > 17) {
                    break;
                }

                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                format.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                String departureTime = format.format(d.getDepartureTime());
                String departureDelay = (!d.getDepartureDelayText().isEmpty()) ? "\u0001" + d.getDepartureDelayText().trim() : "";
                String destination = d.getFinalDestination();
                String opmerking = "";
                //for(int j=0; j<departures.get(i).getRouteText().length(); j++)
                //{
                //    //opmerking += departures.get(i).OpmerkingList1[j];
                //}

                if(destination.length() >= 22) {
                    destination = destination.substring(0,22);
                }

                String vertrekspoor = (d.isChanged()) ? "\u0001"+d.getDepartureTrack() : "\u0003"+d.getDepartureTrack();

                String text = String.format(" %-11s" + "\u0007" + "%-23s %3s", departureTime + departureDelay, destination, vertrekspoor);
                page.setTextOnLine(line, text);
                line++;
                if(d.isChanged()) {
                    opmerking += " Gewijzigd vertrekspoor!";
                }
                if(!opmerking.isEmpty() && !opmerking.equals("geen")) {
                    page.setTextOnLine(line,"\u0001"+opmerking);
                    line++;
                    line++;
                }
            }
        }
        catch(Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }
    }
}