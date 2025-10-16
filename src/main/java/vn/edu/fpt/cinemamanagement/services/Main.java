package vn.edu.fpt.cinemamanagement.services;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();

        int status = 0;
        int nam = now.getYear();
        int thang = now.getMonthValue();
        int ngay = now.getDayOfMonth();
        int gio = now.getHour();
        int phut = now.getMinute();
        int giay = now.getSecond();

        System.out.println("Year: " + nam);
        System.out.println("Month: " + thang);
        System.out.println("Day: " + ngay);
        System.out.println("Hour: " + gio);
        System.out.println("Minute: " + phut);
        System.out.println("Second: " + giay);

        LocalDateTime time_start = LocalDateTime.of(2025, 9, 30,3, 25, 42);
        LocalDateTime time_end = LocalDateTime.of(2025, 9, 30,4, 58, 17);

        if(now.isAfter(time_start)&&now.isBefore(time_end)) {
            System.out.println("La damg chieu do");
            status = 1;
        }else if(now.isBefore(time_start)) {
            System.out.println("Sap chieu");
            status = 0;
        }else if(now.isAfter(time_end)) {
            System.out.println("Chieu xong");
            status = 2;
        }

    }
}