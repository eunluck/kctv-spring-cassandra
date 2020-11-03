package com.kctv.api.common;


import java.text.DecimalFormat;

public class GeoOperations {

    public static final int UPPER_LATITUDE = 0;
    public static final int LOWER_LATITUDE = 1;
    public static final int UPPER_LONGITUDE = 2;
    public static final int LOWER_LONGITUDE = 3;
    private static final double KM_TO_MILES = 0.621371;
    private final double Latitude;
    private final double Longitude;

    double Boundary[];

    public GeoOperations(double init_latitude, double init_longitude) {
        Latitude = init_latitude;
        Longitude = init_longitude;
        Boundary = new double[4];
    }

    public double[] GenerateBoxCoordinates(double Distance) {
        Distance = Distance * KM_TO_MILES;
        double Lat_Factor = (Distance) / 69;
        Boundary[UPPER_LATITUDE] = Latitude + Lat_Factor;
        Boundary[LOWER_LATITUDE] = Latitude - Lat_Factor;


        double Long_Factor = (Distance) / (3960 * 2 * Math.PI / 360 * Math.cos(Latitude));
        Boundary[UPPER_LONGITUDE] = Longitude + Long_Factor;
        Boundary[LOWER_LONGITUDE] = Longitude - Long_Factor;

        for (double x : Boundary) {

            System.out.println(x);


            DecimalFormat df = new DecimalFormat("#.#######");



        }

        return Boundary;

    }


}
