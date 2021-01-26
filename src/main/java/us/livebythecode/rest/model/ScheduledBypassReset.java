package us.livebythecode.rest.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class ScheduledBypassReset {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy", Locale.ENGLISH);
    private long id;
    private LocalDateTime executeTime;
    private long minutesRemaining;
    public ScheduledBypassReset(String input){
        String[] tokens = input.split("\\s+");
        setId(tokens[0]);
        setExecuteTime(tokens[1]+" "+tokens[2]+" "+tokens[3]+" "+tokens[4]+" "+tokens[5]);
    }
    public void setId(String id){
        this.id = Long.parseLong(id);
    }
    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return this.id;
    }

    public void setExecuteTime(String executeTime){
        this.executeTime=LocalDateTime.parse(executeTime, formatter);
        this.minutesRemaining=ChronoUnit.MINUTES.between(LocalDateTime.now(),this.executeTime);
    }
    public LocalDateTime getExecuteTime(){
        return executeTime;
    }
    public long getMinutesRemaining(){
        return ChronoUnit.MINUTES.between(LocalDateTime.now(),this.executeTime);
    }

}
