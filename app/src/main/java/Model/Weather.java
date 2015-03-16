package Model;

import java.lang.String; /**
 * Created by vinhphuc on 3/10/15.
 */

public class Weather {

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getHight() {
        return hight;
    }

    public void setHight(int hight) {
        this.hight = hight;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private int code;
    private String date;
    private String day;
    private int hight;
    private int low;
    private String text;

}

//"code": "30",
//"date": "17 Mar 2015",
//"day": "Tue",
//"high": "91",
//"low": "76",
//"text": "Partly Cloudy"