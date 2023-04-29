package example.grpcclient;

import io.grpc.stub.StreamObserver;
import service.*;
import java.util.ArrayList;
import java.util.List;

public class ZodiacImpl extends ZodiacGrpc.ZodiacImplBase {

    private List<ZodiacEntry> entries = new ArrayList<>();

    @Override
    public void sign(SignRequest request, StreamObserver<SignResponse> responseObserver) {
        String name = request.getName();
        String month = request.getMonth();
        int day = request.getDay();

        //Determine the user's zodiac sign based on their birth month and day
        String sign = determineZodiacSign(month, day);

        //Create a new ZodiacEntry and add it to the list of entries
        ZodiacEntry entry = ZodiacEntry.newBuilder()
                .setName(name)
                .setSign(sign)
                .setMonth(month)
                .setDay(day)
                .build();
        entries.add(entry);

        //Construct a SignResponse with the user's sign and personality traits
        String message = getMessageForSign(sign);

        //Send the response back to the client
        SignResponse response = SignResponse.newBuilder()
                .setIsSuccess(true)
                .setMessage(message)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void find(FindRequest request, StreamObserver<FindResponse> responseObserver) {
        String sign = request.getSign();

        //Find all entries that match the requested sign
        List<ZodiacEntry> matchingEntries = new ArrayList<>();
        for (ZodiacEntry entry : entries) {
            if (entry.getSign().equalsIgnoreCase(sign)) {
                matchingEntries.add(entry);
            }
        }

        //Construct a FindResponse with the matching entries
        FindResponse.Builder builder = FindResponse.newBuilder()
                .setIsSuccess(true);
        if (matchingEntries.isEmpty()) {
            builder.setError("No users found for sign: " + sign);
        } else {
            builder.addAllEntries(matchingEntries);
        }
        FindResponse response = builder.build();

        //Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public String determineZodiacSign(String month, int day) {
        if (month.equals("Jan")) {
            //If day less than 20 -> Capricorn, else Aquarius
            return day < 20 ? "Capricorn" : "Aquarius";
        } else if (month.equals("Feb")) {
            return day < 19 ? "Aquarius" : "Pisces";
        } else if (month.equals("Mar")) {
            return day < 21 ? "Pisces" : "Aries";
        } else if (month.equals("Apr")) {
            return day < 20 ? "Aries" : "Taurus";
        } else if (month.equals("May")) {
            return day < 21 ? "Taurus" : "Gemini";
        } else if (month.equals("Jun")) {
            return day < 21 ? "Gemini" : "Cancer";
        } else if (month.equals("Jul")) {
            return day < 23 ? "Cancer" : "Leo";
        } else if (month.equals("Aug")) {
            return day < 23 ? "Leo" : "Virgo";
        } else if (month.equals("Sep")) {
            return day < 23 ? "Virgo" : "Libra";
        } else if (month.equals("Oct")) {
            return day < 23 ? "Libra" : "Scorpio";
        } else if (month.equals("Nov")) {
            return day < 22 ? "Scorpio" : "Sagittarius";
        } else { // month is "Dec"
            return day < 22 ? "Sagittarius" : "Capricorn";
        }
    }

    private String getMessageForSign(String sign) {
        String message = "";
        switch (sign.toLowerCase()) {
            case "aries":
                message = "Aries, the first sign of the zodiac, belongs to those born between March 21 and April 19th.\n"
                        + "Traits: Energetic, courageous, impulsive, and confident.";
                break;
            case "taurus":
                message = "Taurus, the second sign of the zodiac, belongs to those born between April 20 and May 20th.\n"
                        + "Traits: Patient, reliable, warmhearted, and persistent.";
                break;
            case "gemini":
                message = "Gemini, the third sign of the zodiac, belongs to those born between May 21 and June 20th.\n"
                        + "Traits: Adaptable, outgoing, curious, and intelligent.";
                break;
            case "cancer":
                message = "Cancer, the fourth sign of the zodiac, belongs to those born between June 21 and July 22nd.\n"
                        + "Traits: Emotional, intuitive, sympathetic, and persuasive.";
                break;
            case "leo":
                message = "Leo, the fifth sign of the zodiac, belongs to those born between July 23 and August 22nd.\n"
                        + "Traits: Dramatic, outgoing, fiery, and self-assured.";
                break;
            case "virgo":
                message = "Virgo, the sixth sign of the zodiac, belongs to those born between August 23 and September 22nd.\n"
                        + "Traits: Practical, loyal, gentle, and analytical.";
                break;
            case "libra":
                message = "Libra, the seventh sign of the zodiac, belongs to those born between September 23 and October 22nd.\n"
                        + "Traits: Charming, diplomatic, cooperative, and social.";
                break;
            case "scorpio":
                message = "Scorpio, the eighth sign of the zodiac, belongs to those born between October 23 and November 21st.\n"
                        + "Traits: Passionate, stubborn, resourceful, and brave.";
                break;
            case "sagittarius":
                message = "Sagittarius, the ninth sign of the zodiac, belongs to those born between November 22 and December 21st.\n"
                        + "Traits: Adventurous, optimistic, humorous, and generous.";
                break;
            case "capricorn":
                message = "Capricorn, the tenth sign of the zodiac, belongs to those born between December 22 and January 19th.\n"
                        + "Traits: Responsible, disciplined, self-controlled, and ambitious.";
                break;
            case "aquarius":
                message = "Aquarius, the eleventh sign of the zodiac, belongs to those born between January 20 and February 18th.\n"
                        + "Traits: Progressive, original, independent, and humanitarian.";
                break;
            case "pisces":
                message = "Pisces, the twelfth sign of the zodiac, belongs to those born between February 19 and March 20th.\n"
                        + "Traits: Compassionate, artistic, intuitive, and gentle.";
                break;
            default:
                message = "Unknown sign: " + sign;
        }
        return message;
    }

}
