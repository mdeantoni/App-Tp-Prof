package com.example.matias.tprof.numbers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by Mati on 9/11/2016.
 */
public class NumberFormat {

   private static DecimalFormat format;

    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        format = new DecimalFormat("#,##0.##", decimalFormatSymbols);
    }

    public static String formattedValue(String value) {
        if (value == null || value.isEmpty()|| value.equals("null"))
            return "N/A";
        else{
            try{
            double doubleValue = Double.parseDouble(value);
                return format.format(doubleValue);
            }catch (Exception e){
                return value;
            }
       }

    }

    public static String formattedValue(String prefix, String value) {
        if (value == null || value.isEmpty()|| value.equals("null"))
            return "N/A";
        else{
            try{
                double doubleValue = Double.parseDouble(value);
                return prefix + format.format(doubleValue);
            }catch (Exception e){
                return prefix + value;
            }
        }

    }

    public static String formattedMarketCapValue(String prefix, String value) {
        if (value == null || value.isEmpty()|| value.equals("null"))
            return "N/A";
        else{
            try{
                boolean hasSufix = value.contains("B");
                String newValue = value.replace("B","");
                double doubleValue = Double.parseDouble(newValue);
                return prefix + format.format(doubleValue) + (hasSufix? "MM" : "");
            }catch (Exception e){
                return prefix + value;
            }
        }

    }

    public static String formattedPercentage(String value) {
        if (value == null || value.isEmpty()|| value.equals("null"))
            return "N/A";
        else{
            try{
                boolean hasSufix = value.contains("%");
                String newValue = value.replace("%","").trim();
                double doubleValue = Double.parseDouble(newValue);
                return format.format(doubleValue) + "%" ;
            }catch (Exception e){
                return value;
            }
        }

    }

    public static String formattedValue(double value){
        return format.format(value);
    }

}
