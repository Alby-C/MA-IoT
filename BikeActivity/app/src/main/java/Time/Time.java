package Time;

import static java.lang.String.format;

import androidx.annotation.NonNull;

/**
 * Class to represent a generic time in format hh:mm:ss.
 */
public class Time {
    public int hours = 0;
    public int minutes = 0;
    public int seconds = 0;

    /**
     * Initialize an instance as 00:00:00.
     */
    public Time(){}

    /**
     * Initialize an instance as hours:minutes:seconds.
     */
    public Time(int hours, int minutes, int seconds){
        this.add(seconds + minutes * 60 + hours * 3600);
    }

    /**
     * Initialize an instance converting the number of seconds to the corresponding time.
     */
    public Time(int seconds){
        this.add(seconds);
    }

    /**
     * Add 1 second to the current instance.
     */
    public void increment(){
        this.seconds++;
        if(seconds==60){
            this.seconds = 0;
            this.minutes++;

            if(this.minutes == 60){
                this.minutes = 0;
                this.hours++;
            }
        }
    }

    /**
     * Adds a number of seconds to the current instance.
     * @param secondsToAdd The number of seconds to add.
     */
    public void add(int secondsToAdd){
        int minutes = 0;
        int seconds = (secondsToAdd % 60);

        this.seconds += seconds;
        if(this.seconds>=60){
            this.seconds -= 60;
            minutes++;
        }

        minutes += (secondsToAdd - seconds) / 60;

        this.minutes += minutes % 60;
        if(this.minutes >= 60){
            this.minutes -= 60;
            hours++;
        }

        this.hours += minutes / 60;
    }

    /**
     *Compare two instances of Time.
     * @param a Time to compare.
     * @param b Time to compare.
     * @return Returns a negative integer if a < b, 0 if a = b, a positive integer if a > b.
     * */
    public static int compare(Time a, Time b){
        if((a == null) || (b == null)){
            return 0;
        }

        // Returns the number of seconds of difference
        return (a.hours - b.hours) * 3600 + (a.minutes - b.minutes) * 60 + (a.seconds - b.seconds);
    }

    @NonNull
    public String toString(){
        return format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}