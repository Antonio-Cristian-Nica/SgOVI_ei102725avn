package es.uji.ei1027.ovi.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class RequestSchedule {
    private int reqScheduleID;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    private int dayOfWeek; // 1-7
    private LocalTime startHour;
    private LocalTime endHour;
    private int requestID; // FK → AssistanceRequest

    public int getReqScheduleID() { return reqScheduleID; }
    public void setReqScheduleID(int reqScheduleID) { this.reqScheduleID = reqScheduleID; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartHour() { return startHour; }
    public void setStartHour(LocalTime startHour) { this.startHour = startHour; }

    public LocalTime getEndHour() { return endHour; }
    public void setEndHour(LocalTime endHour) { this.endHour = endHour; }

    public int getRequestID() { return requestID; }
    public void setRequestID(int requestID) { this.requestID = requestID; }
}