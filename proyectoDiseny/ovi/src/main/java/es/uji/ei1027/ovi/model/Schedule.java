package es.uji.ei1027.ovi.model;

import java.time.LocalTime;

public class Schedule {
    private int scheduleID;
    private int dayOfWeek; // 1-7
    private LocalTime startHour;
    private LocalTime endHour;
    private int papID; // FK → PapPati

    public int getScheduleID() { return scheduleID; }
    public void setScheduleID(int scheduleID) { this.scheduleID = scheduleID; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartHour() { return startHour; }
    public void setStartHour(LocalTime startHour) { this.startHour = startHour; }

    public LocalTime getEndHour() { return endHour; }
    public void setEndHour(LocalTime endHour) { this.endHour = endHour; }

    public int getPapID() { return papID; }
    public void setPapID(int papID) { this.papID = papID; }
}